# Simple Dockerfile - Copy pre-built JAR and run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the JAR file (must be built first!)
COPY target/*.jar app.jar

# Expose port
EXPOSE 8761

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
