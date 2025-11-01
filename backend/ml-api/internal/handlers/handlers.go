package handlers

import (
	"errors"
	"log/slog"
	"net/http"

	"github.com/dolmatovDan/FinWise-AI/backend/ml-api/internal/models"
	"github.com/dolmatovDan/FinWise-AI/backend/ml-api/internal/service"
	"github.com/gin-gonic/gin"
	"github.com/go-playground/validator/v10"
)

// MLApiHandler handles HTTP requests to the ML model api
type MlApiHandler struct {
	service *service.MlApiService
	logger  *slog.Logger
}

func NewMlApiHandler(service *service.MlApiService, logger *slog.Logger) *MlApiHandler {
	return &MlApiHandler{
		service: service,
		logger:  logger,
	}
}

type ErrorResponse struct {
	Error   string            `json:"error"`
	Details map[string]string `json:"details,omitempty"`
}

// Forecast handles POST /api/v1/forecast
// @Summary Process a forecast request
// @Tags ml-api
// @Accept json
// @Produce json
// @Param request body models.ForecastRequest true "Forecast request"
// @Success 201 {object} models.ForecastResponse
// @Failure 400 {object} ErrorResponse
// @Failure 500 {object} ErrorResponse
// @Router /api/v1/forecast [post]
func (h *MlApiHandler) Forecast(c *gin.Context) {
	h.logger.Info("handler: forecast request")

	var req models.ForecastRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		h.logger.Warn("handler: failed to bind JSON", "error", err)
		c.JSON(http.StatusBadRequest, ErrorResponse{
			Error:   "Invalid request body",
			Details: parseValidationErrors(err),
		})
		return
	}

	resp, err := h.service.Forecast(c.Request.Context(), &req)
	if err != nil {
		h.logger.Error("handler: failed to process forecast request", "error", err)
		if errors.Is(err, service.ErrValidation) {
			c.JSON(http.StatusBadRequest, ErrorResponse{Error: err.Error()})
			return
		}
		c.JSON(http.StatusInternalServerError, ErrorResponse{Error: "Failed to process forecast request"})
		return
	}

	h.logger.Info("handler: forecast request processed successfully")
	c.JSON(http.StatusOK, resp)
}

// Advice handles POST /api/v1/advice
// @Summary Process an advice request
// @Tags ml-api
// @Accept json
// @Produce json
// @Param request body models.AdviceRequest true "Advice request"
// @Success 201 {object} models.AdviceResponse
// @Failure 400 {object} ErrorResponse
// @Failure 500 {object} ErrorResponse
// @Router /api/v1/advice [post]
func (h *MlApiHandler) Advice(c *gin.Context) {
	h.logger.Info("handler: advice request")

	var req models.AdviceRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		h.logger.Warn("handler: failed to bind JSON", "error", err)
		c.JSON(http.StatusBadRequest, ErrorResponse{
			Error:   "Invalid request body",
			Details: parseValidationErrors(err),
		})
		return
	}

	resp, err := h.service.Advice(c.Request.Context(), &req)
	if err != nil {
		h.logger.Error("handler: failed to process advice request", "error", err)
		if errors.Is(err, service.ErrValidation) {
			c.JSON(http.StatusBadRequest, ErrorResponse{Error: err.Error()})
			return
		}
		c.JSON(http.StatusInternalServerError, ErrorResponse{Error: "Failed to process advice request"})
		return
	}

	h.logger.Info("handler: advice request processed successfully")
	c.JSON(http.StatusOK, resp)
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
