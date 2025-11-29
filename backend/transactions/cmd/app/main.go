package main

import (
	"context"
	"fmt"
	"net/http"
	"os"
	"os/signal"
	"syscall"

	"github.com/dolmatovDan/FinWise-AI/backend/transactions/internal/config"
	"github.com/dolmatovDan/FinWise-AI/backend/transactions/internal/handlers"
	"github.com/dolmatovDan/FinWise-AI/backend/transactions/internal/middleware"
	"github.com/dolmatovDan/FinWise-AI/backend/transactions/internal/service"
	"github.com/dolmatovDan/FinWise-AI/backend/transactions/internal/storage/postgres"
)

// @title FinWise Transactions API
// @version 1.0
// @description API for managing financial transactions with income/expense tracking and profit calculation
// @termsOfService http://swagger.io/terms/

// @contact.name API Support
// @contact.email support@finwise.ai

// @license.name MIT
// @license.url https://opensource.org/licenses/MIT

// @host localhost:8080
// @BasePath /api/v1

// @securityDefinitions.apikey BearerAuth
// @in header
// @name Authorization
// @description Type "Bearer" followed by a space and JWT token.

func main() {
	// Load configuration
	cfg, err := config.Load()
	if err != nil {
		fmt.Fprintf(os.Stderr, "failed to load config: %v\n", err)
		os.Exit(1)
	}

	// Initialize logger
	logger := cfg.Logger.NewLogger()
	logger.Info("starting application", "version", "1.0.0")

	// Connect to database
	ctx := context.Background()
	storage, err := postgres.New(ctx, cfg.Database.DSN(), logger)
	if err != nil {
		logger.Error("failed to connect to database", "error", err)
		os.Exit(1)
	}
	defer storage.Close()

	// Initialize repositories
	transactionRepo := postgres.NewTransactionStorage(storage)

	// Initialize services
	transactionService := service.NewTransactionService(transactionRepo, logger)

	// Initialize JWT validator
	jwtValidator, err := middleware.NewJWTValidator(cfg.JWT.PublicKeyPath, logger)
	if err != nil {
		logger.Error("failed to initialize JWT validator", "error", err)
		os.Exit(1)
	}

	// Initialize auth middleware
	authMiddleware := middleware.NewAuthMiddleware(jwtValidator, logger)

	// Setup HTTP router
	router := handlers.SetupRouter(transactionService, authMiddleware, logger)

	// Create HTTP server
	server := &http.Server{
		Addr:         cfg.Server.Address(),
		Handler:      router,
		ReadTimeout:  cfg.Server.ReadTimeout,
		WriteTimeout: cfg.Server.WriteTimeout,
	}

	// Start server in a goroutine
	go func() {
		logger.Info("starting HTTP server", "address", cfg.Server.Address())
		if err := server.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			logger.Error("server error", "error", err)
			os.Exit(1)
		}
	}()

	// Wait for interrupt signal for graceful shutdown
	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit

	logger.Info("shutting down server...")

	// Create shutdown context with timeout
	shutdownCtx, cancel := context.WithTimeout(context.Background(), cfg.Server.ShutdownTimeout)
	defer cancel()

	// Attempt graceful shutdown
	if err := server.Shutdown(shutdownCtx); err != nil {
		logger.Error("server forced to shutdown", "error", err)
		os.Exit(1)
	}

	logger.Info("server stopped gracefully")
}
