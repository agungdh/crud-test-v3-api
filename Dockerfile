# syntax=docker/dockerfile:1

# ---------- Stage 1: build the executable jar ----------
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

# Resolve dependencies in their own layer so source-only changes reuse it.
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
RUN chmod +x gradlew \
    && ./gradlew --no-daemon --console=plain dependencies > /dev/null

COPY src ./src
RUN ./gradlew --no-daemon --console=plain bootJar \
    && cp build/libs/*.jar application.jar \
    && java -Djarmode=tools -jar application.jar extract --layers --destination extracted

# ---------- Stage 2: runtime (distroless Debian 13, Java 25, non-root) ----------
FROM gcr.io/distroless/java25-debian13:nonroot
WORKDIR /app

# Each COPY is its own layer: dependencies change rarely, application code changes often.
COPY --from=build /workspace/extracted/dependencies/ ./
COPY --from=build /workspace/extracted/spring-boot-loader/ ./
COPY --from=build /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build /workspace/extracted/application/ ./

EXPOSE 8080

# Distroless has no shell, so use exec form. Extra JVM flags can be passed via
# JAVA_TOOL_OPTIONS (e.g. -Duser.timezone=Asia/Jakarta) without overriding these.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-XX:+ExitOnOutOfMemoryError", "-jar", "/app/application.jar"]
