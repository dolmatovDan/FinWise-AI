package handlers

import (
	"log/slog"

	"github.com/dolmatovDan/FinWise-AI/backend/ml-api/internal/service"
	"github.com/gin-gonic/gin"
)

// SetupRouter sets up the Gin router with all routes
func SetupRouter(mlApiService *service.MlApiService, logger *slog.Logger) *gin.Engine {
	router := gin.Default()

	// Health check endpoint
	router.GET("/health", func(c *gin.Context) {
		c.JSON(200, gin.H{"status": "ok"})
	})

	// API v1 routes
	v1 := router.Group("/api/v1")
	{
		// ML API routes
		mlApiHandler := NewMlApiHandler(mlApiService, logger)
		mlApi := v1.Group("/ml-api")
		{
			mlApi.POST("/forecast", mlApiHandler.Forecast)
			mlApi.POST("/advice", mlApiHandler.Advice)
			mlApi.POST("/receipt/scan", mlApiHandler.ReceiptScan) // добавили эндпоинт скана чека
		}
	}

	return router
}
