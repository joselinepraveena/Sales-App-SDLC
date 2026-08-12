from __future__ import annotations

from decimal import Decimal
from typing import Annotated
from uuid import uuid4

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field

app = FastAPI(title="Pricing & Promotions", version="0.1.0")

Price = Annotated[Decimal, Field(gt=0, max_digits=12, decimal_places=2)]


class PriceBookEntry(BaseModel):
    sku: str
    currency: str = Field(min_length=3, max_length=3)
    unit_price: Price
    customer_segment: str = "standard"


class QuoteLine(BaseModel):
    sku: str
    quantity: int = Field(gt=0)


class QuoteRequest(BaseModel):
    customer_segment: str = "standard"
    currency: str = "USD"
    lines: list[QuoteLine]
    promotion_code: str | None = None


class QuoteResponse(BaseModel):
    currency: str
    subtotal: Decimal
    discount: Decimal
    tax: Decimal
    total: Decimal
    promotion_applied: str | None = None


class Promotion(BaseModel):
    code: str
    percent_off: Decimal = Field(ge=0, le=100)
    active: bool = True


price_book: dict[str, PriceBookEntry] = {
    "SKU-100": PriceBookEntry(sku="SKU-100", currency="USD", unit_price=Decimal("249.00")),
}
promotions: dict[str, Promotion] = {}
outbox: list[dict] = []


@app.get("/health/live")
@app.get("/health/ready")
@app.get("/health/startup")
def health() -> dict[str, str]:
    return {"status": "UP"}


@app.put("/api/v1/prices/{sku}")
def upsert_price(sku: str, entry: PriceBookEntry) -> PriceBookEntry:
    stored = entry.model_copy(update={"sku": sku})
    price_book[sku] = stored
    outbox.append(
        {
            "type": "com.sales.pricing.price-changed.v1",
            "sku": sku,
            "currency": stored.currency,
            "unitPrice": float(stored.unit_price),
        }
    )
    return stored


@app.post("/api/v1/promotions")
def activate_promotion(promotion: Promotion) -> Promotion:
    promotions[promotion.code] = promotion
    if promotion.active:
        outbox.append({"type": "com.sales.pricing.promotion-activated.v1", "code": promotion.code})
    return promotion


@app.post("/api/v1/quotes")
def calculate_quote(request: QuoteRequest) -> QuoteResponse:
    subtotal = Decimal("0")
    for line in request.lines:
        entry = price_book.get(line.sku)
        if entry is None:
            raise HTTPException(status_code=404, detail=f"No price for SKU {line.sku}")
        if entry.currency != request.currency:
            raise HTTPException(status_code=409, detail="Currency mismatch")
        subtotal += entry.unit_price * line.quantity

    discount = Decimal("0")
    applied = None
    if request.promotion_code:
        promo = promotions.get(request.promotion_code)
        if promo is None or not promo.active:
            raise HTTPException(status_code=400, detail="Promotion is not active")
        discount = (subtotal * promo.percent_off / Decimal("100")).quantize(Decimal("0.01"))
        applied = promo.code

    taxable = subtotal - discount
    tax = (taxable * Decimal("0.08")).quantize(Decimal("0.01"))
    return QuoteResponse(
        currency=request.currency,
        subtotal=subtotal,
        discount=discount,
        tax=tax,
        total=taxable + tax,
        promotion_applied=applied,
    )


@app.get("/api/v1/events")
def events() -> list[dict]:
    return outbox


def create_app() -> FastAPI:
    return app
