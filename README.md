# Sales-App-SDLC

Enterprise sales application: eight polyglot microservices on Azure Kubernetes Service with a secure, production-grade CI/CD and GitOps workflow.

| Platform | Microsoft Azure and Azure Kubernetes Service (AKS) |
| --- | --- |
| Delivery | GitHub Actions, Docker, Helm, Argo CD, Terraform, and Ansible |
| Quality and security | SonarQube, CodeQL, Trivy, Syft, Cosign, policy-as-code, and automated testing |
| Architecture | Event-driven, API-first, domain-aligned, independently deployable services |

This repository is a **monorepo walking skeleton**. Independent teams can later split services into the repository layout in `docs/architecture.md`. Path filtering, build isolation, and CODEOWNERS keep release ownership explicit while the platform is owned by one team.

## Business capabilities

- Manage prospects, customers, accounts, addresses, contacts, and sales relationships
- Search product catalogues and manage product attributes, bundles, and availability
- Calculate prices, agreements, discounts, promotions, tax, and currency
- Reserve and release inventory across warehouses and channels
- Create, approve, amend, cancel, and track quotations and sales orders
- Authorize, capture, refund, and reconcile payments through external providers
- Send transactional email, SMS, push, and webhook notifications
- Provide dashboards, sales trends, forecasts, and operational reporting

## The eight microservices

| Service | Language | Data store | Key events |
| --- | --- | --- | --- |
| [customer-service](services/customer-service) | Java / Spring Boot | Azure Database for PostgreSQL | CustomerCreated, CustomerUpdated |
| [product-catalog-service](services/product-catalog-service) | TypeScript / NestJS | Azure Cosmos DB + Azure AI Search | ProductPublished, ProductChanged |
| [pricing-service](services/pricing-service) | Python / FastAPI | PostgreSQL + Azure Cache for Redis | PriceChanged, PromotionActivated |
| [inventory-service](services/inventory-service) | Go / Gin | Azure Cosmos DB | InventoryReserved, InventoryReleased, StockChanged |
| [order-service](services/order-service) | C# / ASP.NET Core | Azure SQL Database | OrderCreated, OrderConfirmed, OrderCancelled |
| [payment-service](services/payment-service) | Kotlin / Ktor | Azure SQL Database | PaymentAuthorized, PaymentFailed, RefundCompleted |
| [notification-service](services/notification-service) | Rust / Axum | Azure Cosmos DB | NotificationRequested, NotificationDelivered |
| [sales-analytics-service](services/sales-analytics-service) | Scala / Play Framework | Azure Data Explorer / Fabric | ForecastUpdated |

Polyglot architecture is intentional in this blueprint and expensive in production. Use a different language only where a measurable domain or performance benefit outweighs hiring, patching, and on-call cost.

## Request and event flow

```
Customer browser or mobile app
        |
Azure Front Door Premium + WAF
        |
Azure API Management
        |
AKS Gateway / Ingress
        |
Order Management ---> Customer Service
        |             Product Catalogue
        |             Pricing & Promotions
        |             Inventory
        |             Payment
        |
Azure Service Bus topics and queues
        |
Notification Service + Sales Analytics

Telemetry ---> OpenTelemetry Collector
           ---> Azure Monitor / Application Insights
           ---> Managed Prometheus / Grafana
```

The order service coordinates a saga for inventory reservation and payment authorization. Compensating actions release inventory or reverse payment when downstream processing fails. Each service owns its database and publishes events through a transactional outbox.

## Repository map

```
services/                     independently deployable workloads
apps/web/                     React / Next.js experience
charts/sales-service/         golden Helm chart (probes, HPA, PDB, NetworkPolicy, Key Vault CSI)
gitops/                       Argo CD app-of-apps, ApplicationSet, env overlays
infrastructure/               Terraform modules and env roots (dev/test/stage/prod)
ansible/                      OS/host config for runners and utility VMs only
contracts/                    versioned CloudEvents and OpenAPI
.github/workflows/            reusable CI/CD, path-filtered PR and release pipelines
policies/                     Kyverno admission controls
```

Ansible is **not** the AKS application deployment engine. Kubernetes resources stay declarative in Helm and are reconciled by Argo CD. Terraform owns Azure resources. GitHub Actions owns verification and packaging.

## CI/CD and GitOps

1. Pre-commit: format, lint, unit tests, secret detection
2. Pull request CI: compile, test, SonarQube, CodeQL, Gitleaks, non-release image, Trivy, Syft SBOM, Helm lint
3. Main-branch release: BuildKit image once, SBOM and provenance, scan, Cosign sign, push digest to ACR
4. Helm packaged as a signed OCI artifact
5. Automation opens a GitOps PR with the approved digest
6. Argo CD reconciles AKS without CI cluster credentials
7. Promotion of the **same digest** through development, test, staging, and production
8. Canary via Argo Rollouts; rollback is a GitOps revert

GitHub Actions authenticates to Azure with OIDC. Do not store long-lived Azure credentials.

## Local development

```bash
make contracts
make test-python
make test-go
make test-rust
cp .env.example .env
docker compose up --build
```

Walking-skeleton journey: create a customer, publish a product, calculate a quote, reserve inventory, create an order, authorize payment, dispatch a notification, and read analytics KPIs.

## Delivery phases

1. Foundation — landing zone, identity, ACR, AKS, observability, reusable workflows
2. Walking skeleton — one secure path from source to production-like deployment (this repo)
3. Core commerce — Customer, Product, Pricing, Inventory, Order, Payment with saga and idempotency
4. Engagement and insight — Notification and Analytics
5. Hardening — performance, resilience, recovery, audit evidence
6. Progressive production — limited cohort, canary, then expand traffic

See [docs/architecture.md](docs/architecture.md), [docs/definition-of-done.md](docs/definition-of-done.md), and [docs/sre.md](docs/sre.md).
