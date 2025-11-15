package storage

import (
	"context"

	"github.com/dolmatovDan/FinWise-AI/backend/auth/internal/models"
)

// UserRepository defines the interface for user storage operations
type UserRepository interface {
	// Create creates a new user
	Create(ctx context.Context, user *models.User) error

	// GetByEmail retrieves a user by email
	GetByEmail(ctx context.Context, email string) (*models.User, error)

	// GetByID retrieves a user by ID
	GetByID(ctx context.Context, id int64) (*models.User, error)

	// EmailExists checks if an email already exists
	EmailExists(ctx context.Context, email string) (bool, error)
}

// RefreshTokenRepository defines the interface for refresh token storage operations
type RefreshTokenRepository interface {
	// Save saves a refresh token
	Save(ctx context.Context, token *models.RefreshToken) error

	// GetByTokenHash retrieves a refresh token by its hash
	GetByTokenHash(ctx context.Context, tokenHash string) (*models.RefreshToken, error)

	// Delete deletes a refresh token by its hash
	Delete(ctx context.Context, tokenHash string) error

	// DeleteByUserID deletes all refresh tokens for a user
	DeleteByUserID(ctx context.Context, userID int64) error

	// DeleteExpired deletes all expired refresh tokens
	DeleteExpired(ctx context.Context) error
}
