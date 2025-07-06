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
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        responseWriter = new StringWriter();

        PrintWriter printWriter = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void testDoGet_whenIpParameterIsMissing_returnsBadRequest() throws Exception {
        // Arrange
        when(request.getParameter("ip")).thenReturn(null);

        // Act
        servlet.doGet(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
        response.getWriter().flush();  // flush writer before reading
        String output = responseWriter.toString();
        System.out.println("Servlet Response: " + output);

        // Optionally assert response content
        // assertTrue(output.contains("Missing IP")); // depends on your servlet
    }
}
