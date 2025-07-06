package com.example.geolocator.servlets;

import com.example.geolocator.servlets.GeoLocationServlet; // ✅ This import is required
import io.micrometer.core.instrument.simple.SimpleMeterRegistry; // ✅ This import is required
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GeoLocationServletTest {

    private GeoLocationServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new GeoLocationServlet();
        request = mock(jakarta.servlet.http.HttpServletRequest.class);
        response = mock(jakarta.servlet.http.HttpServletResponse.class);
        responseWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void testDoGet_whenIpParameterIsMissing_returnsBadRequest() throws Exception {
        // Arrange
        when(request.getParameter("ip")).thenReturn(null);

        // Act
        servlet.doGet(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().flush();
        String output = responseWriter.toString();
        System.out.println("Response Output: " + output);

        // Optional assertion if your servlet writes a message like "Missing IP"
        // assertTrue(output.contains("Missing IP"));
    }
}
