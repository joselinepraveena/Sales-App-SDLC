# Ansible

Use Ansible for:

- Hardened self-hosted GitHub Actions runners
- Utility VMs, bastion hosts, and OS baselines
- Approved agents, certificates, and monitoring packages

Do not use Ansible for Kubernetes workload deploys, Azure resource provisioning, or storing application secrets. Retrieve secrets from Key Vault at runtime. Run ansible-lint and Molecule in CI.
