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
variable "tags" { type = map(string) }

resource "azurerm_postgresql_flexible_server" "customer" {
  name                          = "psql-${var.name}-customer"
  location                      = var.location
  resource_group_name           = var.resource_group_name
  sku_name                      = "GP_Standard_D2ds_v5"
  version                       = "16"
  storage_mb                    = 32768
  backup_retention_days         = 14
  geo_redundant_backup_enabled  = true
  public_network_access_enabled = false
  zone                          = "1"
  tags                          = var.tags
  authentication {
    active_directory_auth_enabled = true
    password_auth_enabled         = false
  }
}

resource "azurerm_postgresql_flexible_server" "pricing" {
  name                          = "psql-${var.name}-pricing"
  location                      = var.location
  resource_group_name           = var.resource_group_name
  sku_name                      = "GP_Standard_D2ds_v5"
  version                       = "16"
  storage_mb                    = 32768
  backup_retention_days         = 14
  geo_redundant_backup_enabled  = true
  public_network_access_enabled = false
  zone                          = "2"
  tags                          = var.tags
  authentication {
    active_directory_auth_enabled = true
    password_auth_enabled         = false
  }
}

resource "azurerm_mssql_server" "commerce" {
  name                          = "sql-${var.name}"
  location                      = var.location
  resource_group_name           = var.resource_group_name
  version                       = "12.0"
  public_network_access_enabled = false
  minimum_tls_version           = "1.2"
  tags                          = var.tags
  azuread_administrator {
    login_username              = "sql-admins"
    object_id                   = "00000000-0000-0000-0000-000000000000"
    azuread_authentication_only = true
  }
}

resource "azurerm_mssql_database" "orders" {
  name                 = "orders"
  server_id            = azurerm_mssql_server.commerce.id
  sku_name             = "S2"
  zone_redundant       = false
  storage_account_type = "Geo"
  tags                 = var.tags
}

resource "azurerm_mssql_database" "payments" {
  name                 = "payments"
  server_id            = azurerm_mssql_server.commerce.id
  sku_name             = "S2"
  zone_redundant       = false
  storage_account_type = "Geo"
  tags                 = var.tags
}

resource "azurerm_cosmosdb_account" "this" {
  name                          = "cosmos-${var.name}"
  location                      = var.location
  resource_group_name           = var.resource_group_name
  offer_type                    = "Standard"
  kind                          = "GlobalDocumentDB"
  public_network_access_enabled = false
  local_authentication_disabled = true
  tags                          = var.tags

  consistency_policy {
    consistency_level = "Session"
  }

  geo_location {
    location          = var.location
    failover_priority = 0
    zone_redundant    = true
  }

  capabilities {
    name = "EnableServerless"
  }
}

resource "azurerm_redis_cache" "pricing" {
  name                          = "redis-${var.name}"
  location                      = var.location
  resource_group_name           = var.resource_group_name
  capacity                      = 1
  family                        = "P"
  sku_name                      = "Premium"
  non_ssl_port_enabled          = false
  minimum_tls_version           = "1.2"
  public_network_access_enabled = false
  tags                          = var.tags
}

output "postgres_customer_fqdn" {
  value = azurerm_postgresql_flexible_server.customer.fqdn
}

output "cosmos_endpoint" {
  value = azurerm_cosmosdb_account.this.endpoint
}
