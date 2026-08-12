# syntax=docker/dockerfile:1.7
FROM eclipse-temurin:25-jdk-jammy AS build
WORKDIR /src
COPY . .
RUN apt-get update && apt-get install -y --no-install-recommends unzip \
    && curl -sL https://services.gradle.org/distributions/gradle-8.12-bin.zip -o gradle.zip \
    && unzip -q gradle.zip \
    && ./gradle-8.12/bin/gradle --no-daemon installDist \
    && rm -rf /var/lib/apt/lists/*

FROM eclipse-temurin:25-jre-jammy
WORKDIR /app
RUN groupadd --gid 65532 nonroot && useradd --uid 65532 --gid 65532 --no-create-home nonroot
COPY --from=build /src/build/install/payment-service /app
USER 65532:65532
EXPOSE 8080
ENTRYPOINT ["/app/bin/payment-service"]
