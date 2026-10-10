# ==========================================
# FitAI Backend - Multi-Stage Dockerfile
# Stage 1: Build Application with Maven
# ==========================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Copy Maven descriptor and dependency layer
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B || true

# Copy source code and package application
COPY src src
RUN ./mvnw clean package -DskipTests -B

# ==========================================
# Stage 2: Minimal Production JRE Runtime
# ==========================================
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="FitAI Engineering Team <dev@fitai.com>"
LABEL description="FitAI Spring Boot 3 Backend Service"

WORKDIR /app

# Create unprivileged system user for container security
RUN addgroup -S fitai && adduser -S fitai -G fitai
USER fitai:fitai

# Copy fat JAR from build stage
COPY --from=builder /build/target/fitai-backend-*.jar app.jar

# Expose HTTP port
EXPOSE 8080

# Environment variables with sensible defaults
ENV SPRING_PROFILES_ACTIVE=mysql \
    JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

# Healthcheck monitoring Spring Boot Actuator
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
