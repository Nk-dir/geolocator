package com.example.geolocator.servlets;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

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

    private static final String API_URL = "https://ipapi.co/";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    MeterRegistry meterRegistry;

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

            HttpRequest apiRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + ipAddress))
                    .build();

            HttpResponse<String> apiResponse = httpClient.send(apiRequest, HttpResponse.BodyHandlers.ofString());

            status = "success";
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            out.print(apiResponse.body());
            out.flush();

        } catch (InterruptedException e) {
            status = "interrupted";
            Thread.currentThread().interrupt();
            throw new ServletException("API request was interrupted", e);
        } catch (Exception e) {
            status = "failure";
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to fetch geo-location data\"}");
        } finally {
            if (sample != null && this.meterRegistry != null) {
                sample.stop(this.meterRegistry.timer("geolocator.api.requests", "status", status));
            }
        }
    }

    // Setter for testing
    public void setMeterRegistry(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
}
