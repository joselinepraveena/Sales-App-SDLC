#!/usr/bin/env bash
set -euo pipefail
runtime="${1:-}"
dir="${2:-.}"
cd "$dir"
case "$runtime" in
  java) mvn -q test ;;
  python) python3 -m pytest tests -q ;;
  go) go test ./... ;;
  rust) cargo test ;;
  typescript|node) npm test ;;
  dotnet) dotnet test ;;
  kotlin) ./gradlew test || gradle test ;;
  scala) sbt test ;;
  *) echo "No test runner mapped for $runtime"; exit 0 ;;
esac
