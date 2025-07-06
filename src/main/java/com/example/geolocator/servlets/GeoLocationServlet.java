package com.example.geolocator.servlets;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class GeoLocationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String ip = req.getParameter("ip");
        if (ip == null || ip.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = resp.getWriter();
            out.write("Missing IP parameter");
            return;
        }

        // Your normal logic here
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("IP is: " + ip);
    }
}
