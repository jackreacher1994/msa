# Quarkus 3.15 LTS multi-stage build (customer-core-service, multi-module: domain/application/techframework).
# Default (jvm target): fast Maven JVM build -> slim JRE runtime.
# Native (native target): Mandrel builder -> quarkus-micro-image binary (no JVM).
FROM maven:3.9-eclipse-temurin-21 AS build-jvm
WORKDIR /workspace
ARG MAVEN_MIRROR_URL=
COPY . .
RUN --mount=type=cache,target=/root/.m2/repository \
    if [ -n "$MAVEN_MIRROR_URL" ]; then \
      printf '<settings><mirrors><mirror><id>mirror</id><mirrorOf>central</mirrorOf><url>%s</url></mirror></mirrors></settings>' "$MAVEN_MIRROR_URL" > /root/.m2/settings.xml; \
    fi && \
    mvn -B -q package -DskipTests

FROM quay.io/quarkus/ubi-quarkus-mandrel-builder-image:jdk-21 AS build-native
WORKDIR /workspace
ARG MAVEN_MIRROR_URL=
COPY . .
RUN --mount=type=cache,target=/root/.m2/repository \
    if [ -n "$MAVEN_MIRROR_URL" ]; then \
      printf '<settings><mirrors><mirror><id>mirror</id><mirrorOf>central</mirrorOf><url>%s</url></mirror></mirrors></settings>' "$MAVEN_MIRROR_URL" > /root/.m2/settings.xml; \
    fi && \
    mvn -B -q package -DskipTests -Pnative

FROM quay.io/quarkus/quarkus-micro-image:2.0 AS native
WORKDIR /app
COPY --from=build-native /workspace/techframework/target/*-runner ./application
RUN chmod +x ./application
USER 10001
EXPOSE 8081
ENTRYPOINT ["./application", "-Dquarkus.http.host=0.0.0.0"]

# Default runtime is JVM (fast local builds); use --target native for GraalVM native.
FROM eclipse-temurin:21-jre AS jvm
RUN useradd --system --uid 10001 app
WORKDIR /app
COPY --from=build-jvm /workspace/techframework/target/quarkus-app/ ./
USER 10001
EXPOSE 8081
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["java", "-jar", "/app/quarkus-run.jar"]

FROM jvm AS final
