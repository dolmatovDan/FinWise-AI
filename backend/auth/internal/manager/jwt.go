package manager

import (
	"crypto/rand"
	"crypto/rsa"
	"crypto/sha256"
	"crypto/x509"
	"encoding/base64"
	"encoding/pem"
	"fmt"
	"log/slog"
	"os"
	"time"

	"github.com/dolmatovDan/FinWise-AI/backend/auth/internal/models"
	"github.com/golang-jwt/jwt/v5"
)

// JWTManager handles JWT token operations
type JWTManager struct {
	privateKey      *rsa.PrivateKey
	publicKey       *rsa.PublicKey
	accessTokenTTL  time.Duration
	refreshTokenTTL time.Duration
	logger          *slog.Logger
}

// NewJWTManager creates a new JWT manager instance
func NewJWTManager(privateKeyPath, publicKeyPath string, accessTokenTTL, refreshTokenTTL time.Duration, logger *slog.Logger) (*JWTManager, error) {
	logger.Info("initializing JWT manager", "private_key_path", privateKeyPath, "public_key_path", publicKeyPath)

	privateKey, err := loadPrivateKey(privateKeyPath)
	if err != nil {
		return nil, fmt.Errorf("failed to load private key: %w", err)
	}

	publicKey, err := loadPublicKey(publicKeyPath)
	if err != nil {
		return nil, fmt.Errorf("failed to load public key: %w", err)
	}

	logger.Info("JWT manager initialized successfully")

	return &JWTManager{
		privateKey:      privateKey,
		publicKey:       publicKey,
		accessTokenTTL:  accessTokenTTL,
		refreshTokenTTL: refreshTokenTTL,
		logger:          logger,
	}, nil
}

// GenerateAccessToken generates a new JWT access token
func (jm *JWTManager) GenerateAccessToken(userID int64, email string) (string, error) {
	jm.logger.Info("generating access token", "user_id", userID)

	now := time.Now()
	expiresAt := now.Add(jm.accessTokenTTL)

	claims := &models.JWTClaims{
		UserID: userID,
		Email:  email,
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: jwt.NewNumericDate(expiresAt),
			IssuedAt:  jwt.NewNumericDate(now),
			NotBefore: jwt.NewNumericDate(now),
			Issuer:    "finwise-auth",
			Subject:   fmt.Sprintf("%d", userID),
		},
	}

	token := jwt.NewWithClaims(jwt.SigningMethodRS256, claims)

	tokenString, err := token.SignedString(jm.privateKey)
	if err != nil {
		jm.logger.Error("failed to sign access token", "error", err)
		return "", fmt.Errorf("failed to sign access token: %w", err)
	}

	jm.logger.Info("access token generated successfully", "user_id", userID, "expires_at", expiresAt)
	return tokenString, nil
}

// GenerateRefreshToken generates a random refresh token
func (jm *JWTManager) GenerateRefreshToken() (string, error) {
	jm.logger.Info("generating refresh token")

	b := make([]byte, 32)
	if _, err := rand.Read(b); err != nil {
		jm.logger.Error("failed to generate random bytes", "error", err)
		return "", fmt.Errorf("failed to generate random bytes: %w", err)
	}

	token := base64.URLEncoding.EncodeToString(b)

	jm.logger.Info("refresh token generated successfully")
	return token, nil
}

// HashRefreshToken creates SHA256 hash of refresh token for storage
func (jm *JWTManager) HashRefreshToken(token string) string {
	hash := sha256.Sum256([]byte(token))
	return base64.URLEncoding.EncodeToString(hash[:])
}

// ValidateAccessToken validates JWT access token and returns claims
func (jm *JWTManager) ValidateAccessToken(tokenString string) (*models.JWTClaims, error) {
	jm.logger.Info("validating access token")

	token, err := jwt.ParseWithClaims(tokenString, &models.JWTClaims{}, func(token *jwt.Token) (interface{}, error) {
		if _, ok := token.Method.(*jwt.SigningMethodRSA); !ok {
			return nil, fmt.Errorf("unexpected signing method: %v", token.Header["alg"])
		}
		return jm.publicKey, nil
	})

	if err != nil {
		jm.logger.Warn("failed to parse access token", "error", err)
		return nil, fmt.Errorf("failed to parse token: %w", err)
	}

	if !token.Valid {
		jm.logger.Warn("access token is invalid")
		return nil, fmt.Errorf("invalid token")
	}

	claims, ok := token.Claims.(*models.JWTClaims)
	if !ok {
		jm.logger.Error("failed to extract claims from token")
		return nil, fmt.Errorf("failed to extract claims")
	}

	jm.logger.Info("access token validated successfully", "user_id", claims.UserID)
	return claims, nil
}

// GetRefreshTokenTTL returns refresh token time to live duration
func (jm *JWTManager) GetRefreshTokenTTL() time.Duration {
	return jm.refreshTokenTTL
}

// loadPrivateKey loads RSA private key from file
func loadPrivateKey(path string) (*rsa.PrivateKey, error) {
	keyData, err := os.ReadFile(path)
	if err != nil {
		return nil, fmt.Errorf("failed to read private key file: %w", err)
	}

	block, _ := pem.Decode(keyData)
	if block == nil {
		return nil, fmt.Errorf("failed to decode PEM block containing private key")
	}

	privateKey, err := x509.ParsePKCS8PrivateKey(block.Bytes)
	if err != nil {
		return nil, fmt.Errorf("failed to parse private key: %w", err)
	}
	privateKey1, ok := privateKey.(*rsa.PrivateKey)
	if !ok {
		return nil, fmt.Errorf("failed to parse private key: not an RSA key format")
	}

	return privateKey1, nil
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
