package handlers

import (
	"log/slog"

	"github.com/dolmatovDan/FinWise-AI/backend/auth/internal/manager"
	"github.com/gin-gonic/gin"
)

// SetupRouter sets up the HTTP router with all routes
func SetupRouter(authManager *manager.AuthManager, logger *slog.Logger) *gin.Engine {
	router := gin.Default()

	router.GET("/health", func(c *gin.Context) {
		c.JSON(200, gin.H{"status": "ok"})
	})

	v1 := router.Group("/api/v1")
	{
		authHandler := NewAuthHandler(authManager, logger)
		auth := v1.Group("/auth")
		{
			auth.POST("/register", authHandler.Register)
			auth.POST("/login", authHandler.Login)
			auth.POST("/refresh", authHandler.Refresh)
			auth.POST("/logout", authHandler.Logout)
		}
	}

	return router
}
