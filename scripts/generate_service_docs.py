#!/usr/bin/env python3
"""Generate per-service docs, sonar config, and helm values from the blueprint."""
from pathlib import Path

ROOT = Path("/workspace/services")

SERVICES = [
    {
        "dir": "customer-service",
        "title": "Customer Service",
        "lang": "Java / Spring Boot",
        "store": "Azure Database for PostgreSQL",
        "events": "CustomerCreated, CustomerUpdated",
        "owner": "customer-domain",
        "paths": ["/api/v1/customers"],
    },
    {
        "dir": "product-catalog-service",
        "title": "Product Catalogue",
        "lang": "TypeScript / NestJS",
        "store": "Azure Cosmos DB plus Azure AI Search",
        "events": "ProductPublished, ProductChanged",
        "owner": "product-domain",
        "paths": ["/api/v1/products"],
    },
    {
        "dir": "pricing-service",
        "title": "Pricing & Promotions",
        "lang": "Python / FastAPI",
        "store": "Azure Database for PostgreSQL and Azure Cache for Redis",
        "events": "PriceChanged, PromotionActivated",
        "owner": "pricing-domain",
        "paths": ["/api/v1/quotes", "/api/v1/prices/{sku}", "/api/v1/promotions"],
    },
    {
        "dir": "inventory-service",
        "title": "Inventory",
        "lang": "Go / Gin",
        "store": "Azure Cosmos DB",
        "events": "InventoryReserved, InventoryReleased, StockChanged",
        "owner": "inventory-domain",
        "paths": ["/api/v1/reservations", "/api/v1/stock/{sku}"],
    },
    {
        "dir": "order-service",
        "title": "Order Management",
        "lang": "C# / ASP.NET Core",
        "store": "Azure SQL Database",
        "events": "OrderCreated, OrderConfirmed, OrderCancelled",
        "owner": "order-domain",
        "paths": ["/api/v1/orders"],
    },
    {
        "dir": "payment-service",
        "title": "Payment",
        "lang": "Kotlin / Ktor",
        "store": "Azure SQL Database",
        "events": "PaymentAuthorized, PaymentFailed, RefundCompleted",
        "owner": "payment-domain",
        "paths": ["/api/v1/payments/authorize"],
    },
    {
        "dir": "notification-service",
        "title": "Notification",
        "lang": "Rust / Axum",
        "store": "Azure Cosmos DB",
        "events": "NotificationRequested, NotificationDelivered",
        "owner": "notification-domain",
        "paths": ["/api/v1/notifications"],
    },
    {
        "dir": "sales-analytics-service",
        "title": "Sales Analytics",
        "lang": "Scala / Play Framework",
        "store": "Azure Data Explorer or Microsoft Fabric",
        "events": "Consumes business events; publishes ForecastUpdated",
        "owner": "analytics-domain",
        "paths": ["/api/v1/kpis", "/api/v1/forecasts"],
    },
]


def write(path: Path, content: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content)


for svc in SERVICES:
    base = ROOT / svc["dir"]
    write(
        base / "README.md",
        f"""# {svc['title']}

{svc['lang']} service. Data store: {svc['store']}.

## Responsibility

Independently deployable bounded context. This service never reads or writes another service's database.

## Events

{svc['events']}

Events are written to a transactional outbox in the same commit as the business state change, then published to Azure Service Bus.

## Local run

See the repository root `Makefile` and `docker-compose.yml`. Health endpoints: `/health/live`, `/health/ready`, `/health/startup`.

## Production path

Immutable image digest in ACR, signed Helm chart, GitOps promotion through Argo CD. Secrets come from Azure Key Vault via Workload Identity.
""",
    )
    write(
        base / "docs/runbook.md",
        f"""# {svc['title']} runbook

## Owners

Domain team `{svc['owner']}` with platform on-call for cluster and pipeline failures.

## SLIs

Availability of `{svc['paths'][0]}`, p95 latency, error rate, and queue lag where KEDA is enabled.

## Alerts

- Crash-looping pods or failed readiness
- Elevated 5xx after a canary step
- Outbox depth not draining
- Dependency timeouts to the owned data store

## Rollback

Abort the Argo Rollouts canary or revert the GitOps commit to the previous signed image digest. Do not rebuild the image.

## Recovery

Restore the owned database from the tested backup. Replay Service Bus dead-letter messages using the original idempotency key.
""",
    )
    write(
        base / "docs/threat-model.md",
        f"""# {svc['title']} threat model

## Trust boundaries

- Azure Front Door / APIM to AKS ingress
- Service-to-service calls over NetworkPolicy
- Outbound to {svc['store']} over Private Link
- Azure Service Bus for events

## Key threats

- Privilege escalation via overly broad workload identity
- Injection through public APIs
- Poisoned images if admission policy is bypassed
- Secret leakage in logs or Helm values

## Mitigations

Non-root containers, dropped capabilities, signed digests, Key Vault references, input validation, and least-privilege identities.
""",
    )
    write(
        base / "sonar-project.properties",
        f"""sonar.projectKey=sales-{svc['dir']}
sonar.projectName={svc['title']}
sonar.sources=.
sonar.exclusions=**/node_modules/**,**/target/**,**/dist/**,**/coverage/**
sonar.coverage.exclusions=**/*Test*,**/*.spec.ts,**/tests/**
""",
    )
    write(
        base / "deploy/helm/values.yaml",
        f"""name: {svc['dir']}
image:
  repository: {svc['dir']}
labels:
  owner: {svc['owner']}
  costCenter: cc-sales
  dataClassification: {"restricted" if svc["dir"] == "payment-service" else "confidential"}
config:
  OTEL_SERVICE_NAME: {svc['dir']}
resources:
  requests:
    cpu: 100m
    memory: 256Mi
  limits:
    cpu: 500m
    memory: 512Mi
""",
    )
    write(
        base / "deploy/helm/Chart.yaml",
        f"""apiVersion: v2
name: {svc['dir']}
description: Environment overlay that consumes the golden sales-service chart
type: application
version: 0.1.0
appVersion: "0.1.0"
dependencies:
  - name: sales-service
    version: 0.1.0
    repository: "file://../../../charts/sales-service"
""",
    )

print("generated service docs")
