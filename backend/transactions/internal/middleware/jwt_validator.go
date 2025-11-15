package middleware

import (
	"crypto/rsa"
	"crypto/x509"
	"encoding/pem"
	"fmt"
	"log/slog"
	"os"

	"github.com/dolmatovDan/FinWise-AI/backend/transactions/internal/models"
	"github.com/golang-jwt/jwt/v5"
)

// JWTValidator handles JWT token validation
type JWTValidator struct {
	publicKey *rsa.PublicKey
	logger    *slog.Logger
}

// NewJWTValidator creates a new JWT validator instance
func NewJWTValidator(publicKeyPath string, logger *slog.Logger) (*JWTValidator, error) {
	logger.Info("initializing JWT validator", "public_key_path", publicKeyPath)

	publicKey, err := loadPublicKey(publicKeyPath)
	if err != nil {
		return nil, fmt.Errorf("failed to load public key: %w", err)
	}

	logger.Info("JWT validator initialized successfully")

	return &JWTValidator{
		publicKey: publicKey,
		logger:    logger,
	}, nil
}

// ValidateToken validates JWT access token and returns claims
func (jv *JWTValidator) ValidateToken(tokenString string) (*models.JWTClaims, error) {
	jv.logger.Debug("validating access token")

	token, err := jwt.ParseWithClaims(tokenString, &models.JWTClaims{}, func(token *jwt.Token) (interface{}, error) {
		if _, ok := token.Method.(*jwt.SigningMethodRSA); !ok {
			return nil, fmt.Errorf("unexpected signing method: %v", token.Header["alg"])
		}
		return jv.publicKey, nil
	})

	if err != nil {
		jv.logger.Warn("failed to parse access token", "error", err)
		return nil, fmt.Errorf("failed to parse token: %w", err)
	}

	if !token.Valid {
		jv.logger.Warn("access token is invalid")
		return nil, fmt.Errorf("invalid token")
	}

	claims, ok := token.Claims.(*models.JWTClaims)
	if !ok {
		jv.logger.Error("failed to extract claims from token")
		return nil, fmt.Errorf("failed to extract claims")
	}

	jv.logger.Debug("access token validated successfully", "user_id", claims.UserID)
	return claims, nil
}

// loadPublicKey loads RSA public key from file
func loadPublicKey(path string) (*rsa.PublicKey, error) {
	keyData, err := os.ReadFile(path)
	if err != nil {
		return nil, fmt.Errorf("failed to read public key file: %w", err)
	}

	block, _ := pem.Decode(keyData)
	if block == nil {
		return nil, fmt.Errorf("failed to decode PEM block containing public key")
	}

	pub, err := x509.ParsePKIXPublicKey(block.Bytes)
	if err != nil {
		return nil, fmt.Errorf("failed to parse public key: %w", err)
	}

	publicKey, ok := pub.(*rsa.PublicKey)
	if !ok {
		return nil, fmt.Errorf("not an RSA public key")
	}

	return publicKey, nil
}
