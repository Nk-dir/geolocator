package com.example.geolocator.servlets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Use this to automatically initialize mocks
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

// This annotation automatically handles mock initialization (replaces MockitoAnnotations.openMocks)
@ExtendWith(MockitoExtension.class)
public class GeoLocationServletTest {

    // This annotation creates an instance of the servlet and injects the mocks into it.
    @InjectMocks
    private GeoLocationServlet servlet;

    // These are our mocked objects
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    
    // This object will capture the text written to the response
    private StringWriter stringWriter;

    @BeforeEach
    public void setUp() throws Exception {
        // We set up a StringWriter to act as the response's output stream
        stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        
        // This is the key fix: We tell Mockito what to do when getWriter() is called.
        // It should return our PrintWriter object.
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    public void testDoGet_whenIpParameterIsMissing_returnsBadRequest() throws Exception {
        // ARRANGE: We define the behavior of our mocked request object.
        // When getParameter("ip") is called, it should return null.
        when(request.getParameter("ip")).thenReturn(null);

        // ACT: We execute the method we want to test.
        servlet.doGet(request, response);

        // ASSERT: We check the results.
        // First, verify that setStatus was called on our response mock with the correct code.
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        
        // Second, get the text that was written to our StringWriter.
        String responseOutput = stringWriter.toString();
        
        // Finally, assert that the response text contains the error message.
        assertTrue(responseOutput.contains("IP parameter is missing"));
    }
}
