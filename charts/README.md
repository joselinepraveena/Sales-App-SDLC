# Golden Helm chart

`sales-service` is the shared workload chart. Service overlays live in `services/<name>/deploy/helm` and GitOps env values.

Templates: Deployment, Service, ServiceAccount, HTTPRoute, HPA, KEDA ScaledObject, NetworkPolicy, PDB, ServiceMonitor, SecretProviderClass, ConfigMap, Helm test.

Production images are referenced by digest. Secrets are not stored in values files.
