# Multi-stage build: compile with Maven, run on a slim JRE as a non-root user.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
# Optional Maven mirror (e.g. when Maven Central rate-limits your network).
ARG MAVEN_MIRROR_URL=
COPY . .
RUN --mount=type=cache,target=/root/.m2/repository \
    if [ -n "$MAVEN_MIRROR_URL" ]; then \
      printf '<settings><mirrors><mirror><id>mirror</id><mirrorOf>central</mirrorOf><url>%s</url></mirror></mirrors></settings>' "$MAVEN_MIRROR_URL" > /root/.m2/settings.xml; \
    fi && \
    mvn -B -q package -DskipTests

FROM eclipse-temurin:21-jre
RUN useradd --system --uid 10001 app
WORKDIR /app
COPY --from=build /workspace/target/reference-data-service.jar app.jar
COPY --from=build /workspace/target/otel/opentelemetry-javaagent.jar opentelemetry-javaagent.jar
USER 10001
EXPOSE 8082
# OpenTelemetry Java agent: zero-code, vendor-neutral instrumentation (HTTP, JDBC, JVM, logs) exported via OTLP.
# All exporter settings come from standard OTEL_* environment variables.
ENV JAVA_TOOL_OPTIONS="-javaagent:/app/opentelemetry-javaagent.jar -XX:MaxRAMPercentage=75"
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
