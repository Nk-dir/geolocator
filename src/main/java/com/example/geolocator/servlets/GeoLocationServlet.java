<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_5_0.xsd"
         version="5.0">
    
    <display-name>GeoLocator Web Application</display-name>

    <!-- This gives a direct, explicit order to Tomcat -->
    <servlet>
        <servlet-name>GeoLocationAPIServlet</servlet-name>
        <servlet-class>com.example.geolocator.servlets.GeoLocationServlet</servlet-class>
    </servlet>

    <servlet-mapping>
        <servlet-name>GeoLocationAPIServlet</servlet-name>
        <url-pattern>/api/v1/geolocate</url-pattern>
    </servlet-mapping>
    
</web-app>
