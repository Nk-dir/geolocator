package com.example.geolocator.servlets;

// We only need these imports for a simple servlet test
import org.junit.jupiter.api.Test;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class GeoLocationServletTest {

    @Test
    public void testDoGet_whenIpParameterIsMissing_returnsBadRequest() throws Exception {
        // ARRANGE: Create the servlet instance and mock its dependencies.
        GeoLocationServlet servlet = new GeoLocationServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        // This allows us to capture what the servlet writes in its response.
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        
        // Tell Mockito how our mocked objects should behave.
        when(response.getWriter()).thenReturn(writer);
        when(request.getParameter("ip")).thenReturn(null);

        // ACT: Run the method we want to test.
        servlet.doGet(request, response);

        // ASSERT: Check if the results are what we expect.
        // 1. Verify that the servlet set the HTTP status to "Bad Request".
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        // 2. Verify that the correct error message was written to the response.
        assertTrue(stringWriter.toString().contains("Missing IP parameter"));
    }

    @Test
    public void testDoGet_whenIpParameterIsPresent_returnsOk() throws Exception {
        // ARRANGE: Create the servlet and mocks.
        GeoLocationServlet servlet = new GeoLocationServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        // Tell Mockito how the mocks should behave for a SUCCESSFUL request.
        when(response.getWriter()).thenReturn(writer);
        when(request.getParameter("ip")).thenReturn("1.1.1.1");

        // ACT: Run the method.
        servlet.doGet(request, response);

        // ASSERT: Check the results for a successful case.
        // 1. Verify the HTTP status was "OK".
        verify(response).setStatus(HttpServletResponse.SC_OK);
        // 2. Verify the response contains the correct "echo" text.
        assertTrue(stringWriter.toString().contains("IP is: 1.1.1.1"));
    }
}
