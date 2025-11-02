package handlers

import (
	"errors"
	"log/slog"
	"net/http"

	"github.com/dolmatovDan/FinWise-AI/backend/auth/internal/manager"
	"github.com/dolmatovDan/FinWise-AI/backend/auth/internal/models"
	"github.com/gin-gonic/gin"
	"github.com/go-playground/validator/v10"
)

// AuthHandler handles HTTP requests for authentication
type AuthHandler struct {
	manager *manager.AuthManager
	logger  *slog.Logger
}

// NewAuthHandler creates a new auth handler instance
func NewAuthHandler(manager *manager.AuthManager, logger *slog.Logger) *AuthHandler {
	return &AuthHandler{
		manager: manager,
		logger:  logger,
	}
}

// ErrorResponse represents an error response
type ErrorResponse struct {
	Error   string                 `json:"error"`
	Details map[string]interface{} `json:"details,omitempty"`
}

// Register handles POST /api/v1/auth/register
func (h *AuthHandler) Register(c *gin.Context) {
	h.logger.Info("handler: register request")

	var req models.RegisterRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		h.logger.Warn("handler: failed to bind JSON", "error", err)
		c.JSON(http.StatusBadRequest, ErrorResponse{
			Error:   "Invalid request body",
			Details: parseValidationErrors(err),
		})
		return
	}

	user, err := h.manager.Register(c.Request.Context(), &req)
	if err != nil {
		h.logger.Error("handler: failed to register user", "error", err)
		handleAuthError(c, err)
		return
	}

	h.logger.Info("handler: user registered successfully", "user_id", user.ID, "email", user.Email)
	c.JSON(http.StatusCreated, user)
}

// Login handles POST /api/v1/auth/login
func (h *AuthHandler) Login(c *gin.Context) {
	h.logger.Info("handler: login request")

	var req models.LoginRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		h.logger.Warn("handler: failed to bind JSON", "error", err)
		c.JSON(http.StatusBadRequest, ErrorResponse{
			Error:   "Invalid request body",
			Details: parseValidationErrors(err),
		})
		return
	}

	response, err := h.manager.Login(c.Request.Context(), &req)
	if err != nil {
		h.logger.Error("handler: failed to login", "error", err)
		handleAuthError(c, err)
		return
	}

	h.logger.Info("handler: user logged in successfully", "user_id", response.User.ID)
	c.JSON(http.StatusOK, response)
}

// Refresh handles POST /api/v1/auth/refresh
func (h *AuthHandler) Refresh(c *gin.Context) {
	h.logger.Info("handler: refresh tokens request")

	var req models.RefreshRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		h.logger.Warn("handler: failed to bind JSON", "error", err)
		c.JSON(http.StatusBadRequest, ErrorResponse{
			Error:   "Invalid request body",
			Details: parseValidationErrors(err),
		})
		return
	}

	response, err := h.manager.RefreshTokens(c.Request.Context(), &req)
	if err != nil {
		h.logger.Error("handler: failed to refresh tokens", "error", err)
		handleAuthError(c, err)
		return
	}

	h.logger.Info("handler: tokens refreshed successfully")
	c.JSON(http.StatusOK, response)
}

// Logout handles POST /api/v1/auth/logout
func (h *AuthHandler) Logout(c *gin.Context) {
	h.logger.Info("handler: logout request")

	var req models.LogoutRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		h.logger.Warn("handler: failed to bind JSON", "error", err)
		c.JSON(http.StatusBadRequest, ErrorResponse{
			Error:   "Invalid request body",
			Details: parseValidationErrors(err),
		})
		return
	}

	err := h.manager.Logout(c.Request.Context(), &req)
	if err != nil {
		h.logger.Error("handler: failed to logout", "error", err)
		handleAuthError(c, err)
		return
	}

	h.logger.Info("handler: user logged out successfully")
	c.Status(http.StatusNoContent)
}

// handleAuthError handles different types of auth errors
func handleAuthError(c *gin.Context, err error) {
	var authErr *models.AuthError
	if errors.As(err, &authErr) {
		c.JSON(authErr.HTTPStatusCode(), gin.H{
			"type":    authErr.Type,
			"message": authErr.Message,
			"details": authErr.Details,
		})
		return
	}
	c.JSON(http.StatusInternalServerError, ErrorResponse{
		Error: "Internal server error",
	})
}

// parseValidationErrors parses validation errors and returns a map of field errors
func parseValidationErrors(err error) map[string]interface{} {
	var ve validator.ValidationErrors
	if errors.As(err, &ve) {
		details := make(map[string]interface{})
		for _, fe := range ve {
			details[fe.Field()] = fe.Tag()
		}
		return details
	}
	return nil
}
