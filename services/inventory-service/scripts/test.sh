#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
case "$(basename "$(pwd)")" in
  customer-service) mvn -q test ;;
  product-catalog-service) npm test ;;
  pricing-service) python3 -m pytest tests -q ;;
  inventory-service) go test ./... ;;
  order-service) dotnet test ;;
  payment-service) gradle test || ./gradlew test ;;
  notification-service) cargo test ;;
  sales-analytics-service) sbt test ;;
esac
