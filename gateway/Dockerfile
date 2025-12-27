# Multi-stage build for Gateway

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy parent pom and shared-contracts first for better layer caching
COPY pom.xml ./
COPY shared-contracts/pom.xml ./shared-contracts/
COPY shared-contracts/src ./shared-contracts/src

# Copy gateway
COPY gateway/pom.xml ./gateway/
COPY gateway/src ./gateway/src

# Build shared-contracts first, then gateway
RUN mvn -pl shared-contracts clean install -DskipTests
RUN mvn -pl gateway clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Add non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy JAR from build stage
COPY --from=build /app/gateway/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]

