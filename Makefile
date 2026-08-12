.PHONY: help test test-python test-go test-rust test-node lint contracts compose-up

help:
	@echo "Targets: test, test-python, test-go, test-rust, test-node, contracts, lint"

contracts:
	python3 scripts/validate-contracts.py

test-python:
	python3 -m pip install -q -r services/pricing-service/requirements-dev.txt
	python3 -m pytest services/pricing-service/tests -q

test-go:
	cd services/inventory-service && go test ./...

test-rust:
	cd services/notification-service && cargo test --offline || cargo test

test-node:
	cd services/product-catalog-service && npm install && npm test

test-java:
	cd services/customer-service && mvn -q test

test: contracts test-python test-go test-rust

lint:
	python3 -m compileall services/pricing-service/app scripts
	cd services/inventory-service && gofmt -l .
	ansible-lint ansible || true

compose-up:
	docker compose up --build
