package com.example.geolocator.servlets;

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
        // ARRANGE: Create the servlet instance and mock its dependencies
        GeoLocationServlet servlet = new GeoLocationServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        // This allows us to capture what the servlet writes in its response
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        
        // Tell Mockito how our mocked objects should behave
        when(response.getWriter()).thenReturn(writer);
        when(request.getParameter("ip")).thenReturn(null);

        // ACT: Run the method we want to test
        servlet.doGet(request, response);

        // ASSERT: Check if the results are what we expect
        // 1. Verify that the servlet set the HTTP status to "Bad Request"
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        // 2. Verify that the error message was written to the response
        assertTrue(stringWriter.toString().contains("IP parameter is missing"));
    }
}
