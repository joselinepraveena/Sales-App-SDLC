terraform {
  required_version = ">= 1.7.0"
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 4.0"
    }
    azapi = {
      source  = "azure/azapi"
      version = "~> 2.0"
    }
  }
}

provider "azurerm" {
  features {
    key_vault {
      purge_soft_delete_on_destroy = false
    }
    resource_group {
      prevent_deletion_if_contains_resources = true
    }
  }
}

locals {
  name = "sales-${var.environment}"
  tags = {
    application        = "sales-platform"
    environment        = var.environment
    owner              = "platform"
    costCenter         = "cc-sales"
    dataClassification = "confidential"
  }
}

module "network" {
  source              = "../../modules/network"
  name                = local.name
  location            = var.location
  resource_group_name = azurerm_resource_group.platform.name
  tags                = local.tags
}

module "acr" {
  source              = "../../modules/acr"
  name                = replace("acr${local.name}", "-", "")
  location            = var.location
  resource_group_name = azurerm_resource_group.platform.name
  tags                = local.tags
}

module "key_vault" {
  source              = "../../modules/key-vault"
  name                = "kv-${local.name}"
  location            = var.location
  resource_group_name = azurerm_resource_group.platform.name
  tenant_id           = var.tenant_id
  tags                = local.tags
}

module "aks" {
  source                = "../../modules/aks"
  name                  = "aks-${local.name}"
  location              = var.location
  resource_group_name   = azurerm_resource_group.platform.name
  dns_prefix            = local.name
  subnet_id             = module.network.aks_subnet_id
  acr_id                = module.acr.id
  tenant_id             = var.tenant_id
  tags                  = local.tags
}

module "service_bus" {
  source              = "../../modules/service-bus"
  name                = "sb-${local.name}"
  location            = var.location
  resource_group_name = azurerm_resource_group.platform.name
  tags                = local.tags
}

module "data_services" {
  source              = "../../modules/data-services"
  name                = local.name
  location            = var.location
  resource_group_name = azurerm_resource_group.data.name
  tags                = local.tags
}

module "observability" {
  source              = "../../modules/observability"
  name                = local.name
  location            = var.location
  resource_group_name = azurerm_resource_group.platform.name
  tags                = local.tags
}

resource "azurerm_resource_group" "platform" {
  name     = "rg-${local.name}-platform"
  location = var.location
  tags     = local.tags
}

resource "azurerm_resource_group" "data" {
  name     = "rg-${local.name}-data"
  location = var.location
  tags     = local.tags
}
