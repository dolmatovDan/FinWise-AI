package handlers

import (
	"errors"
	"log/slog"
	"net/http"
	"strings"

	"github.com/dolmatovDan/FinWise-AI/backend/internal/models"
	"github.com/dolmatovDan/FinWise-AI/backend/internal/service"
	"github.com/gin-gonic/gin"
	"github.com/go-playground/validator/v10"
	"github.com/google/uuid"
)

// TransactionHandler handles HTTP requests for transactions
type TransactionHandler struct {
	service *service.TransactionService
	logger  *slog.Logger
}

// NewTransactionHandler creates a new transaction handler instance
func NewTransactionHandler(service *service.TransactionService, logger *slog.Logger) *TransactionHandler {
	return &TransactionHandler{
		service: service,
		logger:  logger,
	}
}

// ErrorResponse represents an error response
type ErrorResponse struct {
	Error   string            `json:"error"`
	Details map[string]string `json:"details,omitempty"`
}

// Create handles POST /api/v1/transactions
// @Summary Create a new transaction
// @Tags transactions
// @Accept json
// @Produce json
// @Param transaction body models.CreateTransactionRequest true "Transaction data"
// @Success 201 {object} models.Transaction
// @Failure 400 {object} ErrorResponse
// @Failure 500 {object} ErrorResponse
// @Router /api/v1/transactions [post]
func (h *TransactionHandler) Create(c *gin.Context) {
	h.logger.Info("handler: create transaction request")

	var req models.CreateTransactionRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		h.logger.Warn("handler: failed to bind JSON", "error", err)
		c.JSON(http.StatusBadRequest, ErrorResponse{
			Error:   "Invalid request body",
			Details: parseValidationErrors(err),
		})
		return
	}

	transaction, err := h.service.Create(c.Request.Context(), &req)
	if err != nil {
		h.logger.Error("handler: failed to create transaction", "error", err)
		if errors.Is(err, service.ErrValidation) {
			c.JSON(http.StatusBadRequest, ErrorResponse{Error: err.Error()})
			return
		}
		c.JSON(http.StatusInternalServerError, ErrorResponse{Error: "Failed to create transaction"})
		return
	}

	h.logger.Info("handler: transaction created successfully", "id", transaction.ID)
	c.JSON(http.StatusCreated, transaction)
}

// GetByID handles GET /api/v1/transactions/:id
// @Summary Get a transaction by ID
// @Tags transactions
// @Produce json
// @Param id path string true "Transaction ID (UUID)"
// @Success 200 {object} models.Transaction
// @Failure 400 {object} ErrorResponse
// @Failure 404 {object} ErrorResponse
// @Failure 500 {object} ErrorResponse
// @Router /api/v1/transactions/{id} [get]
func (h *TransactionHandler) GetByID(c *gin.Context) {
	idStr := c.Param("id")
	h.logger.Info("handler: get transaction by id", "id", idStr)

	id, err := uuid.Parse(idStr)
	if err != nil {
		h.logger.Warn("handler: invalid UUID", "id", idStr, "error", err)
		c.JSON(http.StatusBadRequest, ErrorResponse{Error: "Invalid transaction ID format"})
		return
	}

	transaction, err := h.service.GetByID(c.Request.Context(), id)
	if err != nil {
		h.logger.Error("handler: failed to get transaction", "id", id, "error", err)
		if errors.Is(err, service.ErrNotFound) || strings.Contains(err.Error(), "not found") {
			c.JSON(http.StatusNotFound, ErrorResponse{Error: "Transaction not found"})
			return
		}
		c.JSON(http.StatusInternalServerError, ErrorResponse{Error: "Failed to get transaction"})
		return
	}

	c.JSON(http.StatusOK, transaction)
}

// List handles GET /api/v1/transactions
// @Summary List transactions with filtering and pagination
// @Tags transactions
// @Produce json
// @Param user_id query int false "Filter by user ID"
// @Param type query string false "Filter by type (income/expense)"
// @Param category query string false "Filter by category"
// @Param page query int false "Page number (default: 1)"
// @Param page_size query int false "Page size (default: 10, max: 100)"
// @Success 200 {object} models.TransactionListResponse
// @Failure 400 {object} ErrorResponse
// @Failure 500 {object} ErrorResponse
// @Router /api/v1/transactions [get]
func (h *TransactionHandler) List(c *gin.Context) {
	h.logger.Info("handler: list transactions request")

	var filter models.TransactionFilter
	if err := c.ShouldBindQuery(&filter); err != nil {
		h.logger.Warn("handler: failed to bind query params", "error", err)
		c.JSON(http.StatusBadRequest, ErrorResponse{
			Error:   "Invalid query parameters",
			Details: parseValidationErrors(err),
		})
		return
	}

	// Set default values for pagination if not provided
	if filter.Page == 0 {
		filter.Page = 1
	}
	if filter.PageSize == 0 {
		filter.PageSize = 10
	}

	response, err := h.service.List(c.Request.Context(), &filter)
	if err != nil {
		h.logger.Error("handler: failed to list transactions", "error", err)
		if errors.Is(err, service.ErrValidation) {
			c.JSON(http.StatusBadRequest, ErrorResponse{Error: err.Error()})
			return
		}
		c.JSON(http.StatusInternalServerError, ErrorResponse{Error: "Failed to list transactions"})
		return
	}

	c.JSON(http.StatusOK, response)
}

// Update handles PUT /api/v1/transactions/:id
// @Summary Update a transaction
// @Tags transactions
// @Accept json
// @Produce json
// @Param id path string true "Transaction ID (UUID)"
// @Param transaction body models.UpdateTransactionRequest true "Updated transaction data"
// @Success 200 {object} models.Transaction
// @Failure 400 {object} ErrorResponse
// @Failure 404 {object} ErrorResponse
// @Failure 500 {object} ErrorResponse
// @Router /api/v1/transactions/{id} [put]
func (h *TransactionHandler) Update(c *gin.Context) {
	idStr := c.Param("id")
	h.logger.Info("handler: update transaction request", "id", idStr)

	id, err := uuid.Parse(idStr)
	if err != nil {
		h.logger.Warn("handler: invalid UUID", "id", idStr, "error", err)
		c.JSON(http.StatusBadRequest, ErrorResponse{Error: "Invalid transaction ID format"})
		return
	}

	var req models.UpdateTransactionRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		h.logger.Warn("handler: failed to bind JSON", "error", err)
		c.JSON(http.StatusBadRequest, ErrorResponse{
			Error:   "Invalid request body",
			Details: parseValidationErrors(err),
		})
		return
	}

	transaction, err := h.service.Update(c.Request.Context(), id, &req)
	if err != nil {
		h.logger.Error("handler: failed to update transaction", "id", id, "error", err)
		if errors.Is(err, service.ErrNotFound) || strings.Contains(err.Error(), "not found") {
			c.JSON(http.StatusNotFound, ErrorResponse{Error: "Transaction not found"})
			return
		}
		if errors.Is(err, service.ErrValidation) {
			c.JSON(http.StatusBadRequest, ErrorResponse{Error: err.Error()})
			return
		}
		c.JSON(http.StatusInternalServerError, ErrorResponse{Error: "Failed to update transaction"})
		return
	}

	h.logger.Info("handler: transaction updated successfully", "id", transaction.ID)
	c.JSON(http.StatusOK, transaction)
}

// Delete handles DELETE /api/v1/transactions/:id
// @Summary Delete a transaction
// @Tags transactions
// @Param id path string true "Transaction ID (UUID)"
// @Success 204
// @Failure 400 {object} ErrorResponse
// @Failure 404 {object} ErrorResponse
// @Failure 500 {object} ErrorResponse
// @Router /api/v1/transactions/{id} [delete]
func (h *TransactionHandler) Delete(c *gin.Context) {
	idStr := c.Param("id")
	h.logger.Info("handler: delete transaction request", "id", idStr)

	id, err := uuid.Parse(idStr)
	if err != nil {
		h.logger.Warn("handler: invalid UUID", "id", idStr, "error", err)
		c.JSON(http.StatusBadRequest, ErrorResponse{Error: "Invalid transaction ID format"})
		return
	}

	if err := h.service.Delete(c.Request.Context(), id); err != nil {
		h.logger.Error("handler: failed to delete transaction", "id", id, "error", err)
		if errors.Is(err, service.ErrNotFound) || strings.Contains(err.Error(), "not found") {
			c.JSON(http.StatusNotFound, ErrorResponse{Error: "Transaction not found"})
			return
		}
		c.JSON(http.StatusInternalServerError, ErrorResponse{Error: "Failed to delete transaction"})
		return
	}

	h.logger.Info("handler: transaction deleted successfully", "id", id)
	c.Status(http.StatusNoContent)
}

// parseValidationErrors parses validation errors and returns a map of field errors
func parseValidationErrors(err error) map[string]string {
	var ve validator.ValidationErrors
	if errors.As(err, &ve) {
		details := make(map[string]string)
		for _, fe := range ve {
			details[fe.Field()] = fe.Tag()
		}
		return details
	}
	return nil
}
