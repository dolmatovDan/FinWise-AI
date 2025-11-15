package models

import (
	"time"

	"github.com/golang-jwt/jwt/v5"
	"github.com/google/uuid"
)

// RefreshToken represents a refresh token stored in database
type RefreshToken struct {
	ID        uuid.UUID `json:"id" db:"id"`
	UserID    int64     `json:"user_id" db:"user_id"`
	TokenHash string    `json:"-" db:"token_hash"` // Never expose token hash
	ExpiresAt time.Time `json:"expires_at" db:"expires_at"`
	CreatedAt time.Time `json:"created_at" db:"created_at"`
}

// JWTClaims represents JWT token claims
type JWTClaims struct {
	UserID int64  `json:"user_id"`
	Email  string `json:"email"`
	jwt.RegisteredClaims
}

// RefreshRequest represents request to refresh access token
type RefreshRequest struct {
	RefreshToken string `json:"refresh_token" binding:"required" validate:"required"`
}

// RefreshResponse represents response after refreshing tokens
type RefreshResponse struct {
	AccessToken  string `json:"access_token"`
	RefreshToken string `json:"refresh_token"`
}

// LogoutRequest represents request to logout
type LogoutRequest struct {
	RefreshToken string `json:"refresh_token" binding:"required" validate:"required"`
}
