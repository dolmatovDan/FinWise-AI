package service

import (
	"context"
	"fmt"
	"log/slog"

	mlLauncher "github.com/dolmatovDan/FinWise-AI/backend/ml-api/internal/ml-launcher"
	"github.com/dolmatovDan/FinWise-AI/backend/ml-api/internal/models"
	"github.com/go-playground/validator/v10"
)

// MlApiService handles ML API business logic
type MlApiService struct {
	launcher  mlLauncher.MlLauncher
	logger    *slog.Logger
	validator *validator.Validate
}

// This is a stop sign --> [STOP]
func NewMlApiService(logger *slog.Logger) *MlApiService {
	return &MlApiService{
		launcher:  mlLauncher.DefaultMlLauncher(),
		logger:    logger,
		validator: validator.New(),
	}
}

// Request a forecast (see `models` for details)
func (s *MlApiService) Forecast(ctx context.Context, req *models.ForecastRequest) (*models.ForecastResponse, error) {
	s.logger.Info("service: forecast request", "granularity", req.Granularity, "steps", req.Steps, "model", req.Model)

	// Validate request
	if err := s.validator.Struct(req); err != nil {
		s.logger.Warn("service: validation failed", "error", err)
		return nil, fmt.Errorf("%w: %w", ErrValidation, err)
	}

	out, err := s.launcher.RunForecastModel(req)
	if err != nil {
		s.logger.Error("service: forecast request processing failed", "error", err)
		return nil, fmt.Errorf("%w: %w", ErrMl, err)
	}

	return out, nil
}

// Request an advice
func (s *MlApiService) Advice(ctx context.Context, req *models.AdviceRequest) (*models.AdviceResponse, error) {
	s.logger.Info("service: advice request", "question", req.Question)

	// Validate request
	if err := s.validator.Struct(req); err != nil {
		s.logger.Warn("service: validation failed", "error", err)
		return nil, fmt.Errorf("%w: %w", ErrValidation, err)
	}

	out, err := s.launcher.RunAdviceModel(req)
	if err != nil {
		s.logger.Error("service: advice request processing failed", "error", err)
		return nil, fmt.Errorf("%w: %w", ErrMl, err)
	}

	return out, nil
}

// TODO: receipt scan request
