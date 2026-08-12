# Architecture

Reference architecture for the sales platform. Validate technology choices, regulatory obligations, SLOs, data residency, cost, and skills before production implementation.

## User-facing and edge

- Web: React with Next.js (`apps/web`), Azure Static Web Apps or App Service
- Edge: Azure Front Door Premium with WAF and DDoS protection
- API management: Azure API Management for auth, throttling, quotas, versioning, partner APIs
- Cluster ingress: Gateway API (`HTTPRoute` in the golden Helm chart)
- Identity: Microsoft Entra External ID for customers; Entra ID for workforce
- BFF: optional thin composition only; it must not become a second business domain

## Data and integration

- Database per service; no cross-service database access
- REST for the walking skeleton; OpenAPI contracts live with each service and under `contracts/`
- Azure Service Bus for commands, topics, retries, dead-lettering, and ordered workflows
- Transactional outbox after local commits
- Idempotency keys for orders, payments, inventory reservations, and webhooks
- Versioned CloudEvents in `contracts/events` with backward-compatible evolution
- Redis only for derived pricing data, never as the ledger
- Encryption in transit and at rest; customer-managed keys only when regulation requires them

## Azure platform

| Area | Design |
| --- | --- |
| Hierarchy | Separate subscriptions for connectivity, identity, shared platform, nonproduction, production |
| Network | Hub-and-spoke, private DNS, Azure Firewall, Private Link |
| AKS | Private cluster, Azure CNI Overlay, availability zones, system/user pools, workload identity |
| Registry | ACR Premium, private endpoint, signed images by digest |
| Secrets | Key Vault + Workload Identity + Secrets Store CSI; no secrets in Helm values |
| Policy | Azure Policy plus Kyverno (`policies/kyverno-cluster-policy.yaml`) |
| Observability | OpenTelemetry, Azure Monitor, App Insights, managed Prometheus/Grafana |
| Security | Defender for Cloud/Containers, Sentinel, PIM, Conditional Access |

## AKS workload standards

Encoded in `charts/sales-service`:

- Namespace per bounded context and environment
- Non-root, read-only root filesystem, dropped capabilities, RuntimeDefault seccomp
- Requests/limits, distinct startup/readiness/liveness probes
- HPA and optional KEDA `ScaledObject`
- PodDisruptionBudget and topology spread
- Deny-by-default NetworkPolicy
- Workload Identity annotations on the service account

## Repository strategy

Recommended multi-repo layout when teams are independent:

```
github-org/
  customer-service
  product-catalog-service
  pricing-service
  inventory-service
  order-service
  payment-service
  notification-service
  sales-analytics-service
  sales-platform-infrastructure
  sales-platform-gitops
  enterprise-reusable-workflows
  enterprise-helm-charts
```

This monorepo keeps those boundaries as top-level folders so the walking skeleton can be proven in one place.

## Tool ownership

| Capability | Owner |
| --- | --- |
| Orchestration and verification | GitHub Actions |
| Code quality gate | SonarQube |
| Image creation | Docker BuildKit |
| Immutable artifacts | Azure Container Registry |
| Kubernetes packaging | Helm |
| Cluster reconciliation | Argo CD |
| Azure infrastructure | Terraform |
| Non-Kubernetes hosts | Ansible |

Do not use Ansible to deploy AKS workloads or Terraform to mutate application desired state in GitOps.
