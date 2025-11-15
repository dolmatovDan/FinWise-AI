package postgres

import (
	"context"
	"fmt"
	"log/slog"

	"github.com/dolmatovDan/FinWise-AI/backend/auth/internal/storage"
	"github.com/jackc/pgx/v5/pgxpool"
)

// Storage represents PostgreSQL storage
type Storage struct {
	pool   *pgxpool.Pool
	logger *slog.Logger

	User         storage.UserRepository
	RefreshToken storage.RefreshTokenRepository
}

// New creates a new PostgreSQL storage instance
func New(ctx context.Context, dsn string, logger *slog.Logger) (*Storage, error) {
	logger.Info("connecting to PostgreSQL database")

	config, err := pgxpool.ParseConfig(dsn)
	if err != nil {
		return nil, fmt.Errorf("failed to parse database config: %w", err)
	}

	// Configure pool settings
	config.MaxConns = 25
	config.MinConns = 5

	pool, err := pgxpool.NewWithConfig(ctx, config)
	if err != nil {
		return nil, fmt.Errorf("failed to create connection pool: %w", err)
	}

	// Ping database to verify connection
	if err := pool.Ping(ctx); err != nil {
		pool.Close()
		return nil, fmt.Errorf("failed to ping database: %w", err)
	}

	logger.Info("successfully connected to PostgreSQL database")

	storage := &Storage{
		pool:   pool,
		logger: logger,
	}

	// Initialize repositories
	storage.User = NewUserStorage(storage)
	storage.RefreshToken = NewRefreshTokenStorage(storage)

	return storage, nil
}

// Close closes the database connection pool
func (s *Storage) Close() {
	s.logger.Info("closing database connection pool")
	s.pool.Close()
}

// Pool returns the underlying connection pool
func (s *Storage) Pool() *pgxpool.Pool {
	return s.pool
}
