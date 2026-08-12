# Notification runbook

## Owners

Domain team `notification-domain` with platform on-call for cluster and pipeline failures.

## SLIs

Availability of `/api/v1/notifications`, p95 latency, error rate, and queue lag where KEDA is enabled.

## Alerts

- Crash-looping pods or failed readiness
- Elevated 5xx after a canary step
- Outbox depth not draining
- Dependency timeouts to the owned data store

## Rollback

Abort the Argo Rollouts canary or revert the GitOps commit to the previous signed image digest. Do not rebuild the image.

## Recovery

Restore the owned database from the tested backup. Replay Service Bus dead-letter messages using the original idempotency key.
