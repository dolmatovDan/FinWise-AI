package main

import (
	"context"
	"fmt"
	"net/http"
	"os"
	"os/signal"
	"syscall"

	"github.com/dolmatovDan/FinWise-AI/backend/internal/config"
	"github.com/dolmatovDan/FinWise-AI/backend/internal/handlers"
	"github.com/dolmatovDan/FinWise-AI/backend/internal/service"
	"github.com/dolmatovDan/FinWise-AI/backend/internal/storage/postgres"
)

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

	// Setup HTTP router
	router := handlers.SetupRouter(transactionService, logger)

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
