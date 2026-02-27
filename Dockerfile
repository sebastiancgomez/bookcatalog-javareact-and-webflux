# =========================
# 1️⃣ Build stage
# =========================
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# =========================
# 2️⃣ Runtime stage
# =========================
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Crear usuario no root (best practice cloud)
RUN useradd -ms /bin/bash spring

COPY --from=builder /app/target/*.jar app.jar

# Optimización para contenedores pequeños
ENV JAVA_OPTS="-XX:+UseContainerSupport \
-XX:MaxRAMPercentage=75 \
-XX:+UseG1GC \
-XX:MaxGCPauseMillis=200 \
-Djava.security.egd=file:/dev/./urandom"

USER spring

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]