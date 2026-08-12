# Payment

Kotlin / Ktor service. Data store: Azure SQL Database.

## Responsibility

Independently deployable bounded context. This service never reads or writes another service's database.

## Events

PaymentAuthorized, PaymentFailed, RefundCompleted

Events are written to a transactional outbox in the same commit as the business state change, then published to Azure Service Bus.

## Local run

See the repository root `Makefile` and `docker-compose.yml`. Health endpoints: `/health/live`, `/health/ready`, `/health/startup`.

## Production path

Immutable image digest in ACR, signed Helm chart, GitOps promotion through Argo CD. Secrets come from Azure Key Vault via Workload Identity.
