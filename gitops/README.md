# GitOps

Argo CD app-of-apps (`argocd/app-of-apps.yaml`) plus an ApplicationSet that renders one Application per service and environment.

- Automated sync in development and test
- Pull-request promotion for staging and production
- Image digests in `apps/<env>/<service>.yaml` — never rebuild per environment
- Progressive delivery example in `argocd/rollout-example.yaml`

CI must not use cluster credentials. Argo CD is the only reconciler.
