package main

import (
	"context"
	"fmt"
	"net/http"
	"os"
	"os/signal"
	"syscall"

	"github.com/dolmatovDan/FinWise-AI/backend/auth/internal/config"
	"github.com/dolmatovDan/FinWise-AI/backend/auth/internal/handlers"
	"github.com/dolmatovDan/FinWise-AI/backend/auth/internal/manager"
	"github.com/dolmatovDan/FinWise-AI/backend/auth/internal/storage/postgres"
)

func main() {
	cfg, err := config.Load()
	if err != nil {
		fmt.Fprintf(os.Stderr, "failed to load config: %v\n", err)
		os.Exit(1)
	}

	logger := cfg.Logger.NewLogger()
	logger.Info("starting auth service", "version", "1.0.0")

	ctx := context.Background()
	storage, err := postgres.New(ctx, cfg.Database.DSN(), logger)
	if err != nil {
		logger.Error("failed to connect to database", "error", err)
		os.Exit(1)
	}
	defer storage.Close()

	userRepo := postgres.NewUserStorage(storage)
	refreshTokenRepo := postgres.NewRefreshTokenStorage(storage)

	jwtManager, err := manager.NewJWTManager(
		cfg.JWT.PrivateKeyPath,
		cfg.JWT.PublicKeyPath,
		cfg.JWT.AccessTokenTTL,
		cfg.JWT.RefreshTokenTTL,
		logger,
	)
	if err != nil {
		logger.Error("failed to initialize JWT manager", "error", err)
		os.Exit(1)
	}

	authManager := manager.NewAuthManager(
		userRepo,
		refreshTokenRepo,
		jwtManager,
		logger,
	)

	router := handlers.SetupRouter(authManager, logger)

	server := &http.Server{
		Addr:         cfg.Server.Address(),
		Handler:      router,
		ReadTimeout:  cfg.Server.ReadTimeout,
		WriteTimeout: cfg.Server.WriteTimeout,
	}

	go func() {
		logger.Info("starting HTTP server", "address", cfg.Server.Address())
		if err := server.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			logger.Error("server error", "error", err)
			os.Exit(1)
		}
	}()

	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit

	logger.Info("shutting down server...")

	shutdownCtx, cancel := context.WithTimeout(context.Background(), cfg.Server.ShutdownTimeout)
	defer cancel()

	if err := server.Shutdown(shutdownCtx); err != nil {
		logger.Error("server forced to shutdown", "error", err)
		os.Exit(1)
	}

	logger.Info("server stopped gracefully")
}
