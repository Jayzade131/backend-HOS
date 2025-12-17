# Use OpenJDK 21 base image
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the Spring Boot jar built by Maven
COPY target/*.jar app.jar

# Set Spring profile to 'dev' (environment variable for Spring Boot profile)
ENTRYPOINT ["java","-jar","app.jar","--spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]