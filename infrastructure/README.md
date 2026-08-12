# Terraform infrastructure

Small state boundaries per environment (`environments/dev|test|stage|prod`). Remote Azure Storage backend with locking, encryption, and Azure AD auth.

Modules: network, aks, acr, key-vault, service-bus, data-services, observability.

CI (`terraform-pr.yml`) runs fmt, validate, TFLint, Checkov/Trivy, `terraform test`, and publishes a saved plan. Apply uses the reviewed plan after GitHub Environment approval and OIDC authentication.

Ansible is not used to provision these resources.
