// Replace the entire doGet method with this one

@Override

protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    String ipAddress = req.getParameter("ip");

    

    String status = "unknown"; 

    Timer.Sample sample = null;

    

    // Only start the timer if the meterRegistry actually exists

    if (this.meterRegistry != null) {

        sample = Timer.start(this.meterRegistry);

    }

    

    try {

        if (ipAddress == null || ipAddress.isBlank()) {

            status = "bad_request";

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            resp.getWriter().write("{\"error\":\"IP parameter is missing\"}");

            return; // Exit early

        }



        // ... (The rest of your existing try block logic is unchanged) ...

        HttpRequest apiRequest = HttpRequest.newBuilder()

                .uri(URI.create(API_URL + ipAddress))

                .build();



        HttpResponse<String> apiResponse = httpClient.send(apiRequest, HttpResponse.BodyHandlers.ofString());



        status = "success";

        resp.setStatus(HttpServletResponse.SC_OK);

        resp.setContentType("application/json");

        PrintWriter out = resp.getWriter();

        out.print(apiResponse.body());

        out.flush();



    } catch (InterruptedException e) {

        status = "interrupted";

        Thread.currentThread().interrupt();

        throw new ServletException("API request was interrupted", e);

    } catch (Exception e) {

        status = "failure";

        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        resp.getWriter().write("{\"error\":\"Failed to fetch geo-location data\"}");

    } finally {

        // THIS IS THE CRITICAL FIX:

        // Only try to stop the sample if both the sample AND the meterRegistry exist.

        if (sample != null && this.meterRegistry != null) {

            sample.stop(this.meterRegistry.timer("geolocator.api.requests", "status", status));

        }

    }

}
