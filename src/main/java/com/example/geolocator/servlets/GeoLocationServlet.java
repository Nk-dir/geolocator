package com.example.geolocator.servlets;



import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;

import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;



// Import the necessary Micrometer classes

import io.micrometer.core.instrument.MeterRegistry;

import io.micrometer.core.instrument.Timer;



import Jakarta.servlet.http.HttpServletRequest;

import Jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

import java.io.StringWriter;



import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.*;



// This annotation enables Mockito's dependency injection features

@ExtendWith(MockitoExtension.class)

public class GeoLocationServletTest {



    // THIS IS THE FIX: We create a MOCK of the MeterRegistry.

    // Mockito will create a fake, empty MeterRegistry object for us.

    @Mock

    private MeterRegistry meterRegistry;



    // This tells Mockito: "Create an instance of GeoLocationServlet,

    // and inject the @Mock fields (like meterRegistry) into it."

    @InjectMocks

    private GeoLocationServlet servlet;



    // These are our other mocked objects

    @Mock

    private HttpServletRequest request;

    @Mock

    private HttpServletResponse response;

    

    private StringWriter stringWriter;



    @BeforeEach

    public void setup() throws Exception {

        stringWriter = new StringWriter();

        PrintWriter writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);

    }



    @Test

    public void testDoGet_whenIpParameterIsMissing_returnsBadRequest() throws Exception {

        // ARRANGE

        // Set up the request

        when(request.getParameter("ip")).thenReturn(null);



        // This is a "dummy" timer that we can return when the code asks for one.

        // This prevents NullPointerExceptions inside the Timer logic itself.

        Timer mockTimer = mock(Timer.class);

        when(meterRegistry.timer(anyString(), anyString(), anyString())).thenReturn(mockTimer);

        

        // ACT

        servlet.doGet(request, response);



        // ASSERT

        // Verify that the response status was set correctly

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        

        // Verify that the text was written

        assertTrue(stringWriter.toString().contains("IP parameter is missing"));

        

        // BONUS ASSERT: We can now verify that the metrics code was called!

        // This proves our timer was stopped with the correct status tag.

        verify(meterRegistry).timer("geolocator.api.requests", "status", "bad_request");

    }

}
