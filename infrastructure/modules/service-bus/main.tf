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

resource "azurerm_servicebus_namespace" "this" {
  name                          = var.name
  location                      = var.location
  resource_group_name           = var.resource_group_name
  sku                           = "Premium"
  capacity                      = 1
  premium_messaging_partitions  = 1
  public_network_access_enabled = false
  minimum_tls_version           = "1.2"
  tags                          = var.tags
}

resource "azurerm_servicebus_topic" "sales" {
  name         = "sales.events"
  namespace_id = azurerm_servicebus_namespace.this.id
}

resource "azurerm_servicebus_subscription" "notification" {
  name               = "notification"
  topic_id           = azurerm_servicebus_topic.sales.id
  max_delivery_count = 10
}

resource "azurerm_servicebus_subscription" "analytics" {
  name               = "analytics"
  topic_id           = azurerm_servicebus_topic.sales.id
  max_delivery_count = 10
}

resource "azurerm_servicebus_queue" "order_commands" {
  name                                    = "order.commands"
  namespace_id                            = azurerm_servicebus_namespace.this.id
  dead_lettering_on_message_expiration    = true
  default_message_ttl                     = "P14D"
  lock_duration                           = "PT1M"
  max_delivery_count                      = 10
  requires_duplicate_detection            = true
  duplicate_detection_history_time_window = "PT10M"
}

output "namespace_name" {
  value = azurerm_servicebus_namespace.this.name
}

output "namespace_id" {
  value = azurerm_servicebus_namespace.this.id
}
