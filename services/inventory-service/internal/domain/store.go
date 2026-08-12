package domain

import (
	"errors"
	"sync"

	"github.com/google/uuid"
)

var (
	ErrInsufficientStock = errors.New("insufficient stock")
	ErrReservationMissing = errors.New("reservation not found")
)

type Stock struct {
	SKU         string `json:"sku"`
	WarehouseID string `json:"warehouseId"`
	OnHand      int    `json:"onHand"`
	Reserved    int    `json:"reserved"`
}

func (s Stock) Available() int { return s.OnHand - s.Reserved }

type Reservation struct {
	ID          string `json:"reservationId"`
	SKU         string `json:"sku"`
	WarehouseID string `json:"warehouseId"`
	Quantity    int    `json:"quantity"`
	Status      string `json:"status"`
}

type Event struct {
	Type          string `json:"type"`
	ReservationID string `json:"reservationId,omitempty"`
	SKU           string `json:"sku"`
	Quantity      int    `json:"quantity"`
	WarehouseID   string `json:"warehouseId"`
}

type Store struct {
	mu           sync.Mutex
	stock        map[string]Stock
	reservations map[string]Reservation
	Events       []Event
}

func NewStore() *Store {
	return &Store{
		stock:        map[string]Stock{},
		reservations: map[string]Reservation{},
	}
}

func key(sku, warehouse string) string { return sku + "|" + warehouse }

func (s *Store) Seed(sku, warehouse string, onHand int) {
	s.mu.Lock()
	defer s.mu.Unlock()
	s.stock[key(sku, warehouse)] = Stock{SKU: sku, WarehouseID: warehouse, OnHand: onHand}
}

func (s *Store) Get(sku, warehouse string) (Stock, bool) {
	s.mu.Lock()
	defer s.mu.Unlock()
	item, ok := s.stock[key(sku, warehouse)]
	return item, ok
}

func (s *Store) Reserve(sku, warehouse string, qty int) (Reservation, error) {
	s.mu.Lock()
	defer s.mu.Unlock()
	item, ok := s.stock[key(sku, warehouse)]
	if !ok || item.Available() < qty {
		return Reservation{}, ErrInsufficientStock
	}
	item.Reserved += qty
	s.stock[key(sku, warehouse)] = item
	res := Reservation{ID: uuid.NewString(), SKU: sku, WarehouseID: warehouse, Quantity: qty, Status: "reserved"}
	s.reservations[res.ID] = res
	s.Events = append(s.Events, Event{
		Type: "com.sales.inventory.reserved.v1", ReservationID: res.ID, SKU: sku, Quantity: qty, WarehouseID: warehouse,
	})
	return res, nil
}

func (s *Store) Release(id string) (Reservation, error) {
	s.mu.Lock()
	defer s.mu.Unlock()
	res, ok := s.reservations[id]
	if !ok || res.Status != "reserved" {
		return Reservation{}, ErrReservationMissing
	}
	item := s.stock[key(res.SKU, res.WarehouseID)]
	item.Reserved -= res.Quantity
	s.stock[key(res.SKU, res.WarehouseID)] = item
	res.Status = "released"
	s.reservations[id] = res
	s.Events = append(s.Events, Event{
		Type: "com.sales.inventory.released.v1", ReservationID: id, SKU: res.SKU, Quantity: res.Quantity, WarehouseID: res.WarehouseID,
	})
	return res, nil
}
