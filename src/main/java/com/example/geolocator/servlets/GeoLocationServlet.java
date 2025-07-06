package com.example.geolocator.servlets;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class GeoLocationServlet extends HttpServlet {

    private MeterRegistry meterRegistry;
    private Counter requestCounter;

    public void setMeterRegistry(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.requestCounter = meterRegistry.counter("geolocation_requests_total");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (meterRegistry != null && requestCounter != null) {
            requestCounter.increment();
        }

        String ip = request.getParameter("ip");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if (ip == null || ip.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\": \"IP parameter is missing\"}");
            return;
        }

        // Mock location data (you can replace this with real lookup logic)
        String mockLocation = "{ \"ip\": \"" + ip + "\", \"location\": \"Hyderabad, India\" }";

        response.setStatus(HttpServletResponse.SC_OK);
        out.write(mockLocation);
    }
}
