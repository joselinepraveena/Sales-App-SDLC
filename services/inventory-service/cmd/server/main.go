package main

import (
	"log"
	"net/http"
	"os"

	"github.com/gin-gonic/gin"
	"github.com/sales/inventory-service/internal/api"
	"github.com/sales/inventory-service/internal/domain"
)

func main() {
	store := domain.NewStore()
	store.Seed("SKU-100", "WH-EAST", 50)

	engine := gin.New()
	engine.Use(gin.Recovery())
	api.Register(engine, store)

	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}
	if err := http.ListenAndServe(":"+port, engine); err != nil {
		log.Fatal(err)
	}
}
