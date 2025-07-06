package com.example.geolocator.servlets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class GeoLocationServletTest {

    // The servlet we are going to test
    private GeoLocationServlet servlet;

    // We create MOCKS for all external dependencies
    @Mock
    private MeterRegistry meterRegistry;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    
    private StringWriter stringWriter;

    @BeforeEach
    public void setUp() throws Exception {
        // Initialize all the @Mock objects in this class
        MockitoAnnotations.openMocks(this);

        // Create a real instance of our servlet
        servlet = new GeoLocationServlet();

        // THIS IS THE FIX: Manually inject the mock MeterRegistry into the servlet.
        // We use Java Reflection to set the private 'meterRegistry' field.
        Field meterRegistryField = GeoLocationServlet.class.getDeclaredField("meterRegistry");
        meterRegistryField.setAccessible(true);
        meterRegistryField.set(servlet, meterRegistry);

        // Set up our fake response writer
        stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    public void testDoGet_whenIpParameterIsMissing_returnsBadRequest() throws Exception {
        // ARRANGE
        when(request.getParameter("ip")).thenReturn(null);
        
        // We still need to tell the mock meterRegistry how to behave when called
        Timer mockTimer = mock(Timer.class);
        when(meterRegistry.timer(anyString(), anyString(), anyString())).thenReturn(mockTimer);
        
        // ACT
        servlet.doGet(request, response);

        // ASSERT
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(stringWriter.toString().contains("IP parameter is missing"));
        verify(meterRegistry).timer("geolocator.api.requests", "status", "bad_request");
    }
}
