package com.example.geolocator.servlets;

import com.example.geolocator.servlets.GeoLocationServlet; // ✅ This import is required
import io.micrometer.core.instrument.simple.SimpleMeterRegistry; // ✅ This import is required
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class GeoLocationServletTest {

    private GeoLocationServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    public void setUp() {
        servlet = new GeoLocationServlet();
        servlet.setMeterRegistry(new SimpleMeterRegistry()); // Ensure this setter exists in your servlet

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    public void testDoGet_whenIpParameterIsMissing_returnsBadRequest() throws Exception {
        when(request.getParameter("ip")).thenReturn(null);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        writer.flush();

        String responseContent = stringWriter.toString().trim();
        assertEquals("Missing required 'ip' parameter", responseContent);
    }
}
