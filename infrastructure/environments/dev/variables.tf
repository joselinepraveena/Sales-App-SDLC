variable "location" {
  type        = string
  description = "Azure region for the development landing zone."
  default     = "eastus2"
}

variable "environment" {
  type        = string
  description = "Environment name."
  default     = "dev"
}

variable "tenant_id" {
  type        = string
  description = "Microsoft Entra tenant ID."
}

variable "subscription_id" {
  type        = string
  description = "Azure subscription for the nonproduction platform."
}
