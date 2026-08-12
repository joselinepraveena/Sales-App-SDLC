from decimal import Decimal

from fastapi.testclient import TestClient

from app.main import Promotion, app, price_book, promotions


def test_quote_applies_promotion_and_tax() -> None:
    client = TestClient(app)
    promotions["SAVE10"] = Promotion(code="SAVE10", percent_off=Decimal("10"), active=True)
    price_book["SKU-100"].unit_price = Decimal("100.00")

    response = client.post(
        "/api/v1/quotes",
        json={
            "currency": "USD",
            "promotion_code": "SAVE10",
            "lines": [{"sku": "SKU-100", "quantity": 2}],
        },
    )
    assert response.status_code == 200
    body = response.json()
    assert body["subtotal"] == "200.00"
    assert body["discount"] == "20.00"
    assert body["tax"] == "14.40"
    assert body["total"] == "194.40"


def test_health() -> None:
    client = TestClient(app)
    assert client.get("/health/ready").json()["status"] == "UP"
