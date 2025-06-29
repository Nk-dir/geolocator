package com.example.geolocator.servlets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class GeoLocationServletTest {

    // The servlet we are testing
    private GeoLocationServlet servlet;

    // Mocked (fake) versions of the request and response objects
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    public void setUp() throws Exception {
        // Initialize all the mocked objects
        MockitoAnnotations.openMocks(this);
        servlet = new GeoLocationServlet();

        // This allows us to capture what our servlet writes to the response
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    public void testDoGet_whenIpParameterIsMissing_returnsBadRequest() throws Exception {
        // ARRANGE: We simulate a request with no "ip" parameter
        when(request.getParameter("ip")).thenReturn(null);

        // ACT: We call the doGet method of our servlet
        servlet.doGet(request, response);
        writer.flush(); // Ensure everything is written to our stringWriter

        // ASSERT: We check if the response contains the expected error message
        String responseOutput = stringWriter.toString();
        System.out.println("Test Output: " + responseOutput); // For debugging
        assertTrue(responseOutput.contains("IP parameter is missing"));
    }
}
