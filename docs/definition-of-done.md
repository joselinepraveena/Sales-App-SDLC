# Definition of done

A change is done when all of the following are true:

- Code reviewed; branch protection and CODEOWNERS requirements satisfied
- Unit, integration, contract, and required end-to-end tests pass
- SonarQube Quality Gate, CodeQL, dependency review, secret scan, and license policy pass
- Container and IaC scans meet policy or have approved, time-bound exceptions
- SBOM and provenance generated; image and chart signed and stored by immutable digest
- Helm templates validate; security context, resources, probes, NetworkPolicy, and disruption budget are defined
- OpenTelemetry instrumentation, dashboards, alerts, SLOs, and runbook are available
- Database and event changes are backward compatible and include rollback or roll-forward procedures
- Deployment is promoted through GitOps with required approval and traceable evidence
- Post-deployment checks pass, no critical alerts are open, and rollback has been validated
