# Multi-stage build for Eureka Server

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy parent pom and shared-contracts first for better layer caching
COPY pom.xml ./
COPY shared-contracts/pom.xml ./shared-contracts/
COPY shared-contracts/src ./shared-contracts/src

# Copy eureka-server
COPY eureka-server/pom.xml ./eureka-server/
COPY eureka-server/src ./eureka-server/src

# Build shared-contracts first, then eureka-server
RUN mvn -pl shared-contracts clean install -DskipTests
RUN mvn -pl eureka-server clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Add non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy JAR from build stage
COPY --from=build /app/eureka-server/target/*.jar app.jar

# Expose port
EXPOSE 8761

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8761/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]

