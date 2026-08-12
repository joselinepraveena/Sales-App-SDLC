# syntax=docker/dockerfile:1.7
FROM sbtscala/scala-sbt:eclipse-temurin-21_1.10.6_2.13.15 AS build
WORKDIR /src
COPY . .
RUN sbt stage

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN groupadd --gid 65532 nonroot && useradd --uid 65532 --gid 65532 --no-create-home nonroot
COPY --from=build /src/target/universal/stage /app
USER 65532:65532
EXPOSE 8080
ENTRYPOINT ["/app/bin/sales-analytics-service", "-Dhttp.port=8080"]
