# syntax=docker/dockerfile:1.7
FROM eclipse-temurin:22-jdk-jammy AS build
WORKDIR /src
COPY pom.xml .
COPY src ./src
RUN apt-get update && apt-get install -y --no-install-recommends maven \
    && mvn -q -DskipTests package \
    && rm -rf /var/lib/apt/lists/*

FROM eclipse-temurin:22-jre-jammy
WORKDIR /app
RUN groupadd --gid 65532 nonroot && useradd --uid 65532 --gid 65532 --no-create-home nonroot
COPY --from=build /src/target/customer-service-0.1.0.jar /app/app.jar
USER 65532:65532
EXPOSE 8080
ENTRYPOINT ["java","-XX:+UseContainerSupport","-jar","/app/app.jar"]
