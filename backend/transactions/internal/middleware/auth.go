package middleware

import (
	"log/slog"
	"net/http"
	"strings"

	"github.com/gin-gonic/gin"
)

const (
	authorizationHeader = "Authorization"
	bearerPrefix        = "Bearer "
	userIDKey           = "user_id"
	userEmailKey        = "user_email"
)

// AuthMiddleware creates JWT authentication middleware
type AuthMiddleware struct {
	validator *JWTValidator
	logger    *slog.Logger
}

// NewAuthMiddleware creates a new auth middleware instance
func NewAuthMiddleware(validator *JWTValidator, logger *slog.Logger) *AuthMiddleware {
	return &AuthMiddleware{
		validator: validator,
		logger:    logger,
	}
}

// Authenticate validates JWT token and adds user info to context
func (am *AuthMiddleware) Authenticate() gin.HandlerFunc {
	return func(c *gin.Context) {
		authHeader := c.GetHeader(authorizationHeader)
		if authHeader == "" {
			am.logger.Warn("missing authorization header")
			c.JSON(http.StatusUnauthorized, gin.H{
				"error": "missing authorization header",
			})
			c.Abort()
			return
		}

		if !strings.HasPrefix(authHeader, bearerPrefix) {
			am.logger.Warn("invalid authorization header format")
			c.JSON(http.StatusUnauthorized, gin.H{
				"error": "invalid authorization header format",
			})
			c.Abort()
			return
		}

		token := strings.TrimPrefix(authHeader, bearerPrefix)
		if token == "" {
			am.logger.Warn("empty token")
			c.JSON(http.StatusUnauthorized, gin.H{
				"error": "empty token",
			})
			c.Abort()
			return
		}

		claims, err := am.validator.ValidateToken(token)
		if err != nil {
			am.logger.Warn("invalid token", "error", err)
			c.JSON(http.StatusUnauthorized, gin.H{
				"error": "invalid or expired token",
			})
			c.Abort()
			return
		}

		c.Set(userIDKey, claims.UserID)
		c.Set(userEmailKey, claims.Email)

		am.logger.Debug("user authenticated", "user_id", claims.UserID, "email", claims.Email)

		c.Next()
	}
}

// GetUserID extracts user ID from gin context
func GetUserID(c *gin.Context) (int64, bool) {
	userID, exists := c.Get(userIDKey)
	if !exists {
		return 0, false
	}
	id, ok := userID.(int64)
	return id, ok
}

// GetUserEmail extracts user email from gin context
func GetUserEmail(c *gin.Context) (string, bool) {
	email, exists := c.Get(userEmailKey)
	if !exists {
		return "", false
	}
	emailStr, ok := email.(string)
	return emailStr, ok
}
