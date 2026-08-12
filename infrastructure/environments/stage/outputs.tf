output "aks_id" {
  value = module.aks.id
}

output "acr_login_server" {
  value = module.acr.login_server
}

output "key_vault_uri" {
  value = module.key_vault.vault_uri
}

output "service_bus_namespace" {
  value = module.service_bus.namespace_name
}
