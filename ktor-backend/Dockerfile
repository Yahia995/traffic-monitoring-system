# ===== Build stage =====
FROM gradle:8.14-jdk17 AS builder

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradle.properties gradlew ./
COPY gradle ./gradle

RUN gradle dependencies --no-daemon

COPY src ./src

# Build fat JAR
RUN gradle shadowJar --no-daemon


# ===== Runtime stage =====
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]