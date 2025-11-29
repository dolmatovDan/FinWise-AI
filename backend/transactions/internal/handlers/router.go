package handlers

import (
	"log/slog"

	"github.com/dolmatovDan/FinWise-AI/backend/transactions/internal/middleware"
	"github.com/dolmatovDan/FinWise-AI/backend/transactions/internal/service"
	"github.com/gin-gonic/gin"
	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"

	_ "github.com/dolmatovDan/FinWise-AI/backend/transactions/docs"
)

// SetupRouter sets up the Gin router with all routes
func SetupRouter(transactionService *service.TransactionService, authMiddleware *middleware.AuthMiddleware, logger *slog.Logger) *gin.Engine {
	router := gin.Default()

	router.GET("/health", func(c *gin.Context) {
		c.JSON(200, gin.H{"status": "ok"})
	})

	// Swagger documentation
	router.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))

	v1 := router.Group("/api/v1")
	{
		transactionHandler := NewTransactionHandler(transactionService, logger)
		transactions := v1.Group("/transactions")
		transactions.Use(authMiddleware.Authenticate())
		{
			transactions.POST("", transactionHandler.Create)
			transactions.GET("", transactionHandler.List)
			transactions.GET("/:id", transactionHandler.GetByID)
			transactions.PUT("/:id", transactionHandler.Update)
			transactions.DELETE("/:id", transactionHandler.Delete)
			transactions.POST("/profit", transactionHandler.GetProfit)
		}
	}

	return router
}
