package manager

import (
	"context"
	"fmt"
	"log/slog"
	"time"

	"github.com/dolmatovDan/FinWise-AI/backend/auth/internal/models"
	"github.com/dolmatovDan/FinWise-AI/backend/auth/internal/storage"
	"github.com/go-playground/validator/v10"
	"github.com/google/uuid"
	"golang.org/x/crypto/bcrypt"
)

// AuthManager handles authentication business logic
type AuthManager struct {
	userStorage         storage.UserRepository
	refreshTokenStorage storage.RefreshTokenRepository
	jwtManager          *JWTManager
	logger              *slog.Logger
	validator           *validator.Validate
}

// NewAuthManager creates a new auth manager instance
func NewAuthManager(
	userStorage storage.UserRepository,
	refreshTokenStorage storage.RefreshTokenRepository,
	jwtManager *JWTManager,
	logger *slog.Logger,
) *AuthManager {
	return &AuthManager{
		userStorage:         userStorage,
		refreshTokenStorage: refreshTokenStorage,
		jwtManager:          jwtManager,
		logger:              logger,
		validator:           validator.New(),
	}
}

// Register registers a new user
func (am *AuthManager) Register(ctx context.Context, req *models.RegisterRequest) (*models.User, error) {
	am.logger.Info("manager: registering new user", "email", req.Email)

	if err := am.validator.Struct(req); err != nil {
		am.logger.Warn("manager: validation failed", "error", err)
		return nil, models.NewValidationError("validation failed", map[string]interface{}{
			"details": err.Error(),
		})
	}

	exists, err := am.userStorage.EmailExists(ctx, req.Email)
	if err != nil {
		am.logger.Error("manager: failed to check email existence", "error", err)
		return nil, models.NewInternalError("failed to check email")
	}
	if exists {
		am.logger.Warn("manager: email already exists", "email", req.Email)
		return nil, models.NewConflictError(fmt.Sprintf("user with email %s already exists", req.Email))
	}

	passwordHash, err := am.hashPassword(req.Password)
	if err != nil {
		am.logger.Error("manager: failed to hash password", "error", err)
		return nil, models.NewInternalError("failed to process password")
	}

	user := &models.User{
		Email:        req.Email,
		PasswordHash: passwordHash,
		FullName:     req.FullName,
	}

	if err := am.userStorage.Create(ctx, user); err != nil {
		am.logger.Error("manager: failed to create user", "error", err)
		return nil, models.NewInternalError("failed to create user")
	}

	am.logger.Info("manager: user registered successfully", "id", user.ID, "email", user.Email)
	return user, nil
}

// Login authenticates a user and returns tokens
func (am *AuthManager) Login(ctx context.Context, req *models.LoginRequest) (*models.LoginResponse, error) {
	am.logger.Info("manager: user login attempt", "email", req.Email)

	if err := am.validator.Struct(req); err != nil {
		am.logger.Warn("manager: validation failed", "error", err)
		return nil, models.NewValidationError("validation failed", map[string]interface{}{
			"details": err.Error(),
		})
	}

	user, err := am.userStorage.GetByEmail(ctx, req.Email)
	if err != nil {
		am.logger.Warn("manager: user not found", "email", req.Email)
		return nil, models.NewUnauthorizedError("invalid email or password")
	}

	if err := am.verifyPassword(user.PasswordHash, req.Password); err != nil {
		am.logger.Warn("manager: invalid password", "email", req.Email)
		return nil, models.NewUnauthorizedError("invalid email or password")
	}

	accessToken, refreshToken, err := am.generateTokenPair(ctx, user)
	if err != nil {
		am.logger.Error("manager: failed to generate tokens", "error", err)
		return nil, models.NewInternalError("failed to generate tokens")
	}

	am.logger.Info("manager: user logged in successfully", "user_id", user.ID, "email", user.Email)

	return &models.LoginResponse{
		AccessToken:  accessToken,
		RefreshToken: refreshToken,
		User:         *user,
	}, nil
}

// RefreshTokens refreshes access and refresh tokens
func (am *AuthManager) RefreshTokens(ctx context.Context, req *models.RefreshRequest) (*models.RefreshResponse, error) {
	am.logger.Info("manager: refreshing tokens")

	if err := am.validator.Struct(req); err != nil {
		am.logger.Warn("manager: validation failed", "error", err)
		return nil, models.NewValidationError("validation failed", map[string]interface{}{
			"details": err.Error(),
		})
	}

	tokenHash := am.jwtManager.HashRefreshToken(req.RefreshToken)

	storedToken, err := am.refreshTokenStorage.GetByTokenHash(ctx, tokenHash)
	if err != nil {
		am.logger.Warn("manager: refresh token not found or expired")
		return nil, models.NewUnauthorizedError("invalid or expired refresh token")
	}

	user, err := am.userStorage.GetByID(ctx, storedToken.UserID)
	if err != nil {
		am.logger.Error("manager: failed to get user", "user_id", storedToken.UserID, "error", err)
		return nil, models.NewInternalError("failed to get user")
	}

	if err := am.refreshTokenStorage.Delete(ctx, tokenHash); err != nil {
		am.logger.Warn("manager: failed to delete old refresh token", "error", err)
	}

	accessToken, newRefreshToken, err := am.generateTokenPair(ctx, user)
	if err != nil {
		am.logger.Error("manager: failed to generate new tokens", "error", err)
		return nil, models.NewInternalError("failed to generate tokens")
	}

	am.logger.Info("manager: tokens refreshed successfully", "user_id", user.ID)

	return &models.RefreshResponse{
		AccessToken:  accessToken,
		RefreshToken: newRefreshToken,
	}, nil
}

// Logout logs out a user by invalidating their refresh token
func (am *AuthManager) Logout(ctx context.Context, req *models.LogoutRequest) error {
	am.logger.Info("manager: user logout")

	if err := am.validator.Struct(req); err != nil {
		am.logger.Warn("manager: validation failed", "error", err)
		return models.NewValidationError("validation failed", map[string]interface{}{
			"details": err.Error(),
		})
	}

	tokenHash := am.jwtManager.HashRefreshToken(req.RefreshToken)

	if err := am.refreshTokenStorage.Delete(ctx, tokenHash); err != nil {
		am.logger.Warn("manager: failed to delete refresh token", "error", err)
		return models.NewUnauthorizedError("invalid refresh token")
	}

	am.logger.Info("manager: user logged out successfully")
	return nil
}

// generateTokenPair generates both access and refresh tokens
func (am *AuthManager) generateTokenPair(ctx context.Context, user *models.User) (string, string, error) {
	accessToken, err := am.jwtManager.GenerateAccessToken(user.ID, user.Email)
	if err != nil {
		return "", "", fmt.Errorf("failed to generate access token: %w", err)
	}

	refreshToken, err := am.jwtManager.GenerateRefreshToken()
	if err != nil {
		return "", "", fmt.Errorf("failed to generate refresh token: %w", err)
	}

	tokenHash := am.jwtManager.HashRefreshToken(refreshToken)

	expiresAt := time.Now().Add(am.jwtManager.GetRefreshTokenTTL())
	if err := am.refreshTokenStorage.Save(ctx, &models.RefreshToken{
		ID:        uuid.New(),
		UserID:    user.ID,
		TokenHash: tokenHash,
		ExpiresAt: expiresAt,
	}); err != nil {
		return "", "", fmt.Errorf("failed to save refresh token: %w", err)
	}

	return accessToken, refreshToken, nil
}

// hashPassword hashes a password using bcrypt
func (am *AuthManager) hashPassword(password string) (string, error) {
	hash, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
	if err != nil {
		return "", err
	}
	return string(hash), nil
}

// verifyPassword verifies a password against a hash
func (am *AuthManager) verifyPassword(hash, password string) error {
	return bcrypt.CompareHashAndPassword([]byte(hash), []byte(password))
}
