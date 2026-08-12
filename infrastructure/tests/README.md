# Terraform tests

Add `*.tftest.hcl` next to modules once an Azure subscription and backend are available. CI should run `terraform test` after `fmt`, `validate`, TFLint, and Checkov.
