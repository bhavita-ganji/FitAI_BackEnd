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

# Ensure mvnw has execute permissions and unix line endings
RUN chmod +x mvnw && sed -i 's/\r$//' mvnw

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

# Expose HTTP ports (standard 8080 and Render default 10000)
EXPOSE 8080 10000

# Environment variables tuned for Render cloud container limits
ENV SPRING_PROFILES_ACTIVE=postgres \
    JAVA_OPTS="-Xms128m -Xmx384m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"

# Healthcheck monitoring Spring Boot Actuator
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:${PORT:-8080}/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar"]
