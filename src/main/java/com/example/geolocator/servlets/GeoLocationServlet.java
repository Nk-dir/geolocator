package com.example.geolocator.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Timer;
import io.micrometer.graphite.GraphiteConfig;
import io.micrometer.graphite.GraphiteMeterRegistry;

// NOTE: We have removed the @WebServlet annotation to rely on web.xml
public class GeoLocationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String API_URL = "http://ip-api.com/json/";
    
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private GraphiteMeterRegistry meterRegistry;

    @Override
    public void init() throws ServletException {
        super.init();
        
        GraphiteConfig config = new GraphiteConfig() {
            @Override
            public String host() {
                return System.getenv().getOrDefault("GRAPHITE_HOST", "localhost");
            }
            
            @Override
            public int port() {
                return Integer.parseInt(System.getenv().getOrDefault("GRAPHITE_PORT", "2003"));
            }

            @Override
            public String get(String key) {
                return null; // Not needed
            }
        };

        this.meterRegistry = new GraphiteMeterRegistry(config, Clock.SYSTEM);
        System.out.println("Micrometer Graphite registry initialized for real server environment.");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ipAddress = req.getParameter("ip");

        // THIS IS THE FIX: Check if meterRegistry exists before using it.
        Timer.Sample sample = (this.meterRegistry != null) ? Timer.start(this.meterRegistry) : null;

        if (ipAddress == null || ipAddress.isBlank()) {
            if (sample != null) {
                sample.stop(this.meterRegistry.timer("geolocator.api.requests", "status", "bad_request"));
            }
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"IP parameter is missing\"}");
            return;
        }

        try {
            HttpRequest apiRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + ipAddress))
                    .build();

            HttpResponse<String> apiResponse = httpClient.send(apiRequest, HttpResponse.BodyHandlers.ofString());

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            out.print(apiResponse.body());
            out.flush();

            if (sample != null) {
                sample.stop(this.meterRegistry.timer("geolocator.api.requests", "status", "success"));
            }

        } catch (InterruptedException e) {
            if (sample != null) {
                sample.stop(this.meterRegistry.timer("geolocator.api.requests", "status", "interrupted"));
            }
            Thread.currentThread().interrupt();
            throw new ServletException("API request was interrupted", e);
        } catch (Exception e) {
            if (sample != null) {
                sample.stop(this.meterRegistry.timer("geolocator.api.requests", "status", "failure"));
            }
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to fetch geo-location data\"}");
        }
    }
}