FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="Vitaliy Shvetsov <vitaliy@clickenrent.nl>"
LABEL description="Click & Rent Search Service - Elasticsearch-based global search"

WORKDIR /app

# Copy the built JAR file
COPY target/search-service-1.0-SNAPSHOT.jar app.jar

# Expose the service port
EXPOSE 8086

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
