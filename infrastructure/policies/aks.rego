package aks.security

deny[msg] {
  input.resource_type == "azurerm_kubernetes_cluster"
  not input.values.private_cluster_enabled
  msg := "AKS clusters must be private."
}

deny[msg] {
  input.resource_type == "azurerm_kubernetes_cluster"
  not input.values.oidc_issuer_enabled
  msg := "AKS must enable OIDC issuer for workload identity."
}

deny[msg] {
  input.resource_type == "azurerm_container_registry"
  input.values.sku != "Premium"
  msg := "ACR must be Premium for private endpoints and content trust."
}
