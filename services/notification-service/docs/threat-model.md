# Notification threat model

## Trust boundaries

- Azure Front Door / APIM to AKS ingress
- Service-to-service calls over NetworkPolicy
- Outbound to Azure Cosmos DB over Private Link
- Azure Service Bus for events

## Key threats

- Privilege escalation via overly broad workload identity
- Injection through public APIs
- Poisoned images if admission policy is bypassed
- Secret leakage in logs or Helm values

## Mitigations

Non-root containers, dropped capabilities, signed digests, Key Vault references, input validation, and least-privilege identities.
