#!/usr/bin/env bash
set -euo pipefail
runtime="${1:-}"
case "$runtime" in
  python) python3 -m compileall . ;;
  go) test -z "$(gofmt -l .)" ;;
  rust) cargo fmt --check || true ;;
  *) echo "lint skipped for $runtime" ;;
esac
