# Observability and SRE

Instrument every service with OpenTelemetry traces, metrics, and structured logs. Propagate correlation, trace, customer-safe transaction, and idempotency identifiers.

## SLIs and SLOs

Define indicators for availability, latency, correctness, freshness, and queue processing. Set SLOs and error budgets by user journey (quote-to-cash), not only by individual service.

Suggested journey SLOs for the walking skeleton:

| Journey | Availability | Latency |
| --- | --- | --- |
| Create customer | 99.9% | p95 < 300 ms |
| Calculate quote | 99.9% | p95 < 400 ms |
| Confirm order | 99.95% | p95 < 800 ms |
| Authorize payment | 99.95% | p95 < 700 ms |
| Dispatch notification | 99.5% | p95 < 2 s |

Payment and order ledgers have stricter RTO/RPO than analytics.

## Golden signals

Latency, traffic, errors, saturation, plus business KPIs: order confirmation rate and payment success rate.

## Alerts

Actionable alerts include owners, severity, runbook links, escalation, and suppression rules. The golden Helm chart emits Prometheus scrapes via `ServiceMonitor`.

## Cost control

Sample traces, tier retention, structured fields only, and never emit secrets, PAN, or raw tokens.
