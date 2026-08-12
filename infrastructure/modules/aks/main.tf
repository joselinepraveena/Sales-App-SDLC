terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 4.0"
    }
  }
}

variable "name" { type = string }
variable "location" { type = string }
variable "resource_group_name" { type = string }
variable "dns_prefix" { type = string }
variable "subnet_id" { type = string }
variable "acr_id" { type = string }
variable "tenant_id" { type = string }
variable "tags" { type = map(string) }

resource "azurerm_kubernetes_cluster" "this" {
  name                    = var.name
  location                = var.location
  resource_group_name     = var.resource_group_name
  dns_prefix              = var.dns_prefix
  private_cluster_enabled = true
  sku_tier                = "Standard"
  oidc_issuer_enabled     = true
  workload_identity_enabled = true
  azure_policy_enabled    = true
  image_cleaner_enabled   = true
  node_os_upgrade_channel = "SecurityPatch"
  tags                    = var.tags

  default_node_pool {
    name                         = "system"
    vm_size                      = "Standard_D4s_v5"
    vnet_subnet_id               = var.subnet_id
    only_critical_addons_enabled = true
    os_sku                       = "AzureLinux"
    zones                        = ["1", "2", "3"]
    auto_scaling_enabled         = true
    min_count                    = 3
    max_count                    = 5
  }

  identity {
    type = "SystemAssigned"
  }

  network_profile {
    network_plugin      = "azure"
    network_plugin_mode = "overlay"
    network_policy      = "cilium"
    load_balancer_sku   = "standard"
    outbound_type       = "userDefinedRouting"
  }

  oms_agent {
    log_analytics_workspace_id = azurerm_log_analytics_workspace.aks.id
  }

  microsoft_defender {
    log_analytics_workspace_id = azurerm_log_analytics_workspace.aks.id
  }

  key_vault_secrets_provider {
    secret_rotation_enabled = true
  }
}

resource "azurerm_kubernetes_cluster_node_pool" "user" {
  name                  = "user"
  kubernetes_cluster_id = azurerm_kubernetes_cluster.this.id
  vm_size               = "Standard_D8s_v5"
  vnet_subnet_id        = var.subnet_id
  mode                  = "User"
  os_sku                = "AzureLinux"
  zones                 = ["1", "2", "3"]
  auto_scaling_enabled  = true
  min_count             = 3
  max_count             = 12
  tags                  = var.tags
}

resource "azurerm_log_analytics_workspace" "aks" {
  name                = "log-${var.name}"
  location            = var.location
  resource_group_name = var.resource_group_name
  sku                 = "PerGB2018"
  retention_in_days   = 30
  tags                = var.tags
}

resource "azurerm_role_assignment" "acr_pull" {
  scope                = var.acr_id
  role_definition_name = "AcrPull"
  principal_id         = azurerm_kubernetes_cluster.this.kubelet_identity[0].object_id
}

output "id" {
  value = azurerm_kubernetes_cluster.this.id
}

output "oidc_issuer_url" {
  value = azurerm_kubernetes_cluster.this.oidc_issuer_url
}
