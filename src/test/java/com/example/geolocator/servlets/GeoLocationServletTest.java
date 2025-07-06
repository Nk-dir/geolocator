package com.example.geolocator.servlets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// THIS IS THE MODERN WAY TO INITIALIZE MOCKS. IT'S REQUIRED.
@ExtendWith(MockitoExtension.class)
public class GeoLocationServletTest {

    // This tells Mockito to create a FAKE MeterRegistry.
    @Mock
    private MeterRegistry meterRegistry;

    // This tells Mockito: 
    // 1. Create a REAL GeoLocationServlet instance.
    // 2. Find its fields, and INJECT the @Mock objects into it.
    @InjectMocks
    private GeoLocationServlet servlet;

    // These are also mocked dependencies.
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    
    private StringWriter stringWriter;

    @BeforeEach
    public void setUp() throws Exception {
        // We only need to set up the response writer here.
        // @ExtendWith and @InjectMocks handle everything else automatically.
        stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    public void testDoGet_whenIpParameterIsMissing_returnsBadRequest() throws Exception {
        // ARRANGE
        when(request.getParameter("ip")).thenReturn(null);
        
        // We still need to tell our FAKE meterRegistry how to behave when a timer is requested.
        // We just tell it to return another fake Timer object.
        Timer mockTimer = mock(Timer.class);
        when(meterRegistry.timer(anyString(), anyString(), anyString())).thenReturn(mockTimer);
        
        // ACT
        servlet.doGet(request, response);

        // ASSERT
        // Verify the servlet interacted with the response mock correctly.
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        // Verify the servlet wrote the correct error message.
        assertTrue(stringWriter.toString().contains("IP parameter is missing"));
        // Verify the servlet interacted with the meterRegistry mock correctly.
        verify(meterRegistry).timer("geolocator.api.requests", "status", "bad_request");
    }
}
