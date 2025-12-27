# Multi-stage build for Support Service

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy parent pom and shared-contracts first for better layer caching
COPY pom.xml ./
COPY shared-contracts/pom.xml ./shared-contracts/
COPY shared-contracts/src ./shared-contracts/src

# Copy support-service
COPY support-service/pom.xml ./support-service/
COPY support-service/src ./support-service/src

# Build shared-contracts first, then support-service
RUN mvn -pl shared-contracts clean install -DskipTests
RUN mvn -pl support-service clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Add non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy JAR from build stage
COPY --from=build /app/support-service/target/*.jar app.jar

# Expose port
EXPOSE 8085

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=5 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8085/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]

