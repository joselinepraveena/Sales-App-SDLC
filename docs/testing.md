# Testing strategy

| Layer | Purpose |
| --- | --- |
| Unit | Business logic; run on every change |
| Component | Service with framework; Testcontainers where a real database is required |
| Contract | OpenAPI and CloudEvent compatibility (`scripts/validate-contracts.py`) |
| Integration | Emulators locally; dedicated Azure environments where behavior differs |
| End-to-end | Customer → quote → reserve → order → payment → notification |
| Performance | k6 (`scripts/k6-quote-to-cash.js`) |
| Resilience | Dependency latency, queue backlog, node/zone loss |
| Security | SAST, SCA, secret scan, IaC/container scan, selected DAST |
| Recovery | Database restore and message replay |
