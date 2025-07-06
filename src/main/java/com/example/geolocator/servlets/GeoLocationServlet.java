package com.example.geolocator.servlets;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.graphite.GraphiteMeterRegistry;
import io.micrometer.core.instrument.Clock;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeoLocationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String API_URL = "http://ip-api.com/json/";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private MeterRegistry meterRegistry;

    // This public method allows our unit test to inject a mock MeterRegistry.
    public void setMeterRegistry(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    // This method runs when the real Tomcat server starts the servlet.
    @Override
    public void init() throws ServletException {
        super.init();
        GraphiteConfig config = new GraphiteConfig() {
            @Override public String get(String key) { return null; }
            @Override public String host() { return System.getenv().getOrDefault("GRAPHITE_HOST", "localhost"); }
            @Override public int port() { return Integer.parseInt(System.getenv().getOrDefault("GRAPHITE_PORT", "2003")); }
        };
        this.meterRegistry = new GraphiteMeterRegistry(config, Clock.SYSTEM);
        System.out.println("Micrometer Graphite registry initialized for real server environment.");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ipAddress = req.getParameter("ip");
        String status = "unknown";
        Timer.Sample sample = null;

        if (this.meterRegistry != null) {
            sample = Timer.start(this.meterRegistry);
        }

        try {
            if (ipAddress == null || ipAddress.isBlank()) {
                status = "bad_request";
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"IP parameter is missing\"}");
                return;
            }

            HttpRequest apiRequest = HttpRequest.newBuilder().uri(URI.create(API_URL + ipAddress)).build();
            HttpResponse<String> apiResponse = httpClient.send(apiRequest, HttpResponse.BodyHandlers.ofString());

            status = "success";
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            // THIS LINE NOW PRINTS THE REAL JSON RESPONSE FROM THE API
            out.print(apiResponse.body());
            out.flush();

        } catch (InterruptedException e) {
            status = "interrupted";
            Thread.currentThread().interrupt();
            throw new ServletException("API request was interrupted", e);
        } catch (Exception e) {
            status = "failure";
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to fetch geo-location data: " + e.getMessage() + "\"}");
        } finally {
            if (sample != null && meterRegistry != null) {
                sample.stop(meterRegistry.timer("geolocator.api.requests", "status", status));
            }
        }
    }
}
