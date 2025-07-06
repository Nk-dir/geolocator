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

public class GeoLocationServletTest {

    @Test
    public void testDoGet_whenIpParameterIsMissing_returnsBadRequest() throws Exception {
        // Arrange
        GeoLocationServlet servlet = new GeoLocationServlet();
        MeterRegistry meterRegistry = mock(MeterRegistry.class);
        Timer mockTimer = mock(Timer.class);
        when(meterRegistry.timer(anyString(), anyString(), anyString())).thenReturn(mockTimer);
        
        // This line calls the new method, fixing the NullPointerException.
        servlet.setMeterRegistry(meterRegistry);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        
        when(request.getParameter("ip")).thenReturn(null);
        when(response.getWriter()).thenReturn(writer);

        // Act
        servlet.doGet(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(stringWriter.toString().contains("IP parameter is missing"));
        verify(meterRegistry).timer("geolocator.api.requests", "status", "bad_request");
    }
}
