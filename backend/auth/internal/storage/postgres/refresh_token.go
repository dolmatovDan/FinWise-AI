package postgres

import (
	"context"
	"errors"
	"fmt"
	"time"

	"github.com/dolmatovDan/FinWise-AI/backend/auth/internal/models"
	"github.com/dolmatovDan/FinWise-AI/backend/auth/internal/storage"
	"github.com/jackc/pgx/v5"
)

// RefreshTokenStorage handles refresh token database operations
type RefreshTokenStorage struct {
	storage *Storage
}

// Compile-time check to ensure RefreshTokenStorage implements storage.RefreshTokenRepository
var _ storage.RefreshTokenRepository = (*RefreshTokenStorage)(nil)

// NewRefreshTokenStorage creates a new refresh token storage instance
func NewRefreshTokenStorage(storage *Storage) *RefreshTokenStorage {
	return &RefreshTokenStorage{
		storage: storage,
	}
}

// Save saves a refresh token
func (rts *RefreshTokenStorage) Save(ctx context.Context, token *models.RefreshToken) error {
	rts.storage.logger.Info("saving refresh token", "user_id", token.UserID)

	query := `
		INSERT INTO refresh_token (user_id, token_hash, expires_at)
		VALUES ($1, $2, $3)
		RETURNING id, created_at
	`

	err := rts.storage.pool.QueryRow(ctx, query,
		token.UserID,
		token.TokenHash,
		token.ExpiresAt,
	).Scan(
		&token.ID,
		&token.CreatedAt,
	)

	if err != nil {
		rts.storage.logger.Error("failed to save refresh token", "error", err)
		return fmt.Errorf("failed to save refresh token: %w", err)
	}

	rts.storage.logger.Info("refresh token saved successfully", "id", token.ID, "user_id", token.UserID)
	return nil
}

// GetByTokenHash retrieves a refresh token by its hash
func (rts *RefreshTokenStorage) GetByTokenHash(ctx context.Context, tokenHash string) (*models.RefreshToken, error) {
	rts.storage.logger.Info("fetching refresh token by hash")

	query := `
		SELECT id, user_id, token_hash, expires_at, created_at
		FROM refresh_token
		WHERE token_hash = $1
	`

	var token models.RefreshToken
	err := rts.storage.pool.QueryRow(ctx, query, tokenHash).Scan(
		&token.ID,
		&token.UserID,
		&token.TokenHash,
		&token.ExpiresAt,
		&token.CreatedAt,
	)

	if err != nil {
		if errors.Is(err, pgx.ErrNoRows) {
			rts.storage.logger.Info("refresh token not found")
			return nil, fmt.Errorf("refresh token not found")
		}

		rts.storage.logger.Error("failed to fetch refresh token", "error", err)
		return nil, fmt.Errorf("failed to fetch refresh token: %w", err)
	}

	// Check if token is expired
	if token.ExpiresAt.Before(time.Now()) {
		rts.storage.logger.Info("refresh token is expired", "expires_at", token.ExpiresAt)
		return nil, fmt.Errorf("refresh token is expired")
	}

	rts.storage.logger.Info("refresh token fetched successfully", "id", token.ID)
	return &token, nil
}

// Delete deletes a refresh token by its hash
func (rts *RefreshTokenStorage) Delete(ctx context.Context, tokenHash string) error {
	rts.storage.logger.Info("deleting refresh token by hash")

	query := `
		DELETE FROM refresh_token
		WHERE token_hash = $1
	`

	result, err := rts.storage.pool.Exec(ctx, query, tokenHash)
	if err != nil {
		rts.storage.logger.Error("failed to delete refresh token", "error", err)
		return fmt.Errorf("failed to delete refresh token: %w", err)
	}

	if result.RowsAffected() == 0 {
		rts.storage.logger.Info("refresh token not found for deletion")
		return fmt.Errorf("refresh token not found")
	}

	rts.storage.logger.Info("refresh token deleted successfully")
	return nil
}

// DeleteByUserID deletes all refresh tokens for a user
func (rts *RefreshTokenStorage) DeleteByUserID(ctx context.Context, userID int64) error {
	rts.storage.logger.Info("deleting all refresh tokens for user", "user_id", userID)

	query := `
		DELETE FROM refresh_token
		WHERE user_id = $1
	`

	result, err := rts.storage.pool.Exec(ctx, query, userID)
	if err != nil {
		rts.storage.logger.Error("failed to delete refresh tokens", "error", err)
		return fmt.Errorf("failed to delete refresh tokens: %w", err)
	}

	rts.storage.logger.Info("refresh tokens deleted successfully", "user_id", userID, "count", result.RowsAffected())
	return nil
}

// DeleteExpired deletes all expired refresh tokens
func (rts *RefreshTokenStorage) DeleteExpired(ctx context.Context) error {
	rts.storage.logger.Info("deleting expired refresh tokens")

	query := `
		DELETE FROM refresh_token
		WHERE expires_at < NOW()
	`

	result, err := rts.storage.pool.Exec(ctx, query)
	if err != nil {
		rts.storage.logger.Error("failed to delete expired refresh tokens", "error", err)
		return fmt.Errorf("failed to delete expired refresh tokens: %w", err)
	}

	rts.storage.logger.Info("expired refresh tokens deleted successfully", "count", result.RowsAffected())
	return nil
}
