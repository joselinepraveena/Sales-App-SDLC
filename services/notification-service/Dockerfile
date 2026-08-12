# syntax=docker/dockerfile:1.7
FROM rust:1.83-bookworm AS build
WORKDIR /src
COPY Cargo.toml ./
RUN mkdir src && echo "fn main() {}" > src/main.rs && cargo build --release && rm -rf src
COPY src ./src
RUN cargo build --release

FROM gcr.io/distroless/cc-debian12:nonroot
COPY --from=build /src/target/release/notification-service /notification-service
USER 65532:65532
EXPOSE 8080
ENTRYPOINT ["/notification-service"]
