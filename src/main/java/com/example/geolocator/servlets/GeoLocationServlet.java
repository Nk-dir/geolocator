// src/main/java/com/example/geolocator/servlets/GeoLocationServlet.java
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

// This annotation registers the servlet, so we don't need a web.xml entry
public class GeoLocationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String API_URL = "http://ip-api.com/json/";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ipAddress = req.getParameter("ip");

        if (ipAddress == null || ipAddress.isBlank()) {
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

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServletException("API request was interrupted", e);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to fetch geo-location data\"}");
        }
    }
}
