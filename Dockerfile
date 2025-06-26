# Stage 1: Build the WAR
FROM maven:3.8-jdk-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install

# Stage 2: Tomcat + Deployment
FROM tomcat:9.0-jdk11-temurin
RUN rm -rf /usr/local/tomcat/webapps/ROOT
COPY --from=build /app/target/geolocator.war /usr/local/tomcat/webapps/geolocator.war
EXPOSE 8080
