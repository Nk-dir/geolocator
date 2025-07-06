package com.example.geolocator.servlets;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GeoLocationServletTest {

    @Mock
    private MeterRegistry meterRegistry;

    @Test
    public void testDoGet_whenIpParameterIsMissing_returnsBadRequest() throws Exception {
        GeoLocationServlet servlet = new GeoLocationServlet();
        servlet.setMeterRegistry(meterRegistry); // Inject mock

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);
        when(request.getParameter("ip")).thenReturn(null);

        Timer mockTimer = mock(Timer.class);
        when(meterRegistry.timer(anyString(), anyString(), anyString())).thenReturn(mockTimer);

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(stringWriter.toString().contains("IP parameter is missing"));
        verify(meterRegistry).timer("geolocator.api.requests", "status", "bad_request");
    }
}
