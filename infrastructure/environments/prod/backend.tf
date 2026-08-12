terraform {
  backend "azurerm" {
    resource_group_name  = "rg-sales-tfstate"
    storage_account_name = "stsalesplatformtf"
    container_name       = "tfstate"
    key                  = "prod.terraform.tfstate"
    use_azuread_auth     = true
  }
}
