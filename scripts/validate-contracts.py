#!/usr/bin/env python3
"""Validate versioned CloudEvent contracts used by all sales services."""
from __future__ import annotations

import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ENVELOPE = json.loads((ROOT / "contracts/events/cloudevents.json").read_text())
REQUIRED_TYPES = {
    "com.sales.customer.created.v1",
    "com.sales.customer.updated.v1",
    "com.sales.product.published.v1",
    "com.sales.product.changed.v1",
    "com.sales.pricing.price-changed.v1",
    "com.sales.pricing.promotion-activated.v1",
    "com.sales.inventory.reserved.v1",
    "com.sales.inventory.released.v1",
    "com.sales.inventory.stock-changed.v1",
    "com.sales.order.created.v1",
    "com.sales.order.confirmed.v1",
    "com.sales.order.cancelled.v1",
    "com.sales.payment.authorized.v1",
    "com.sales.payment.failed.v1",
    "com.sales.payment.refund-completed.v1",
    "com.sales.notification.requested.v1",
    "com.sales.notification.delivered.v1",
    "com.sales.analytics.forecast-updated.v1",
}


def main() -> int:
    declared = set(ENVELOPE["properties"]["type"]["enum"])
    missing = REQUIRED_TYPES - declared
    extra = declared - REQUIRED_TYPES
    if missing or extra:
        print(f"contract drift missing={sorted(missing)} extra={sorted(extra)}", file=sys.stderr)
        return 1
    sample = {
        "specversion": "1.0",
        "id": "evt-1",
        "source": "order-service",
        "type": "com.sales.order.created.v1",
        "time": "2026-08-12T00:00:00Z",
        "datacontenttype": "application/json",
        "data": {"orderId": "o-1", "customerId": "c-1", "status": "Draft"},
    }
    for field in ENVELOPE["required"]:
        if field not in sample:
            print(f"sample event missing {field}", file=sys.stderr)
            return 1
    print(f"validated {len(declared)} event types")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
