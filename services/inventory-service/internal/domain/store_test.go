package domain

import "testing"

func TestReserveAndRelease(t *testing.T) {
	store := NewStore()
	store.Seed("SKU-100", "WH-EAST", 10)

	res, err := store.Reserve("SKU-100", "WH-EAST", 3)
	if err != nil {
		t.Fatal(err)
	}
	stock, _ := store.Get("SKU-100", "WH-EAST")
	if stock.Available() != 7 {
		t.Fatalf("available = %d, want 7", stock.Available())
	}

	if _, err := store.Reserve("SKU-100", "WH-EAST", 8); err == nil {
		t.Fatal("expected insufficient stock")
	}

	if _, err := store.Release(res.ID); err != nil {
		t.Fatal(err)
	}
	stock, _ = store.Get("SKU-100", "WH-EAST")
	if stock.Available() != 10 {
		t.Fatalf("available after release = %d, want 10", stock.Available())
	}
	if store.Events[0].Type != "com.sales.inventory.reserved.v1" {
		t.Fatalf("unexpected event %s", store.Events[0].Type)
	}
}
