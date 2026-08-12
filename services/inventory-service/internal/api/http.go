package api

import (
	"errors"
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/sales/inventory-service/internal/domain"
)

type reserveRequest struct {
	SKU         string `json:"sku" binding:"required"`
	WarehouseID string `json:"warehouseId" binding:"required"`
	Quantity    int    `json:"quantity" binding:"required,gt=0"`
}

func Register(engine *gin.Engine, store *domain.Store) {
	engine.GET("/health/live", health)
	engine.GET("/health/ready", health)
	engine.GET("/health/startup", health)
	engine.GET("/api/v1/stock/:sku", func(c *gin.Context) {
		item, ok := store.Get(c.Param("sku"), c.DefaultQuery("warehouseId", "WH-EAST"))
		if !ok {
			c.JSON(http.StatusNotFound, gin.H{"error": "stock not found"})
			return
		}
		c.JSON(http.StatusOK, gin.H{"sku": item.SKU, "warehouseId": item.WarehouseID, "onHand": item.OnHand, "reserved": item.Reserved, "available": item.Available()})
	})
	engine.POST("/api/v1/reservations", func(c *gin.Context) {
		var req reserveRequest
		if err := c.ShouldBindJSON(&req); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
			return
		}
		res, err := store.Reserve(req.SKU, req.WarehouseID, req.Quantity)
		if errors.Is(err, domain.ErrInsufficientStock) {
			c.JSON(http.StatusConflict, gin.H{"error": err.Error()})
			return
		}
		c.JSON(http.StatusCreated, res)
	})
	engine.POST("/api/v1/reservations/:id/release", func(c *gin.Context) {
		res, err := store.Release(c.Param("id"))
		if err != nil {
			c.JSON(http.StatusNotFound, gin.H{"error": err.Error()})
			return
		}
		c.JSON(http.StatusOK, res)
	})
}

func health(c *gin.Context) {
	c.JSON(http.StatusOK, gin.H{"status": "UP"})
}
