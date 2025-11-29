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

// NewMlApiService создает сервис и втыкает туда лаунчер (HTTP на HF)
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

	for _, tr := range req.Transactions {
		if err := s.validator.Struct(tr); err != nil {
			s.logger.Warn("service: validation failed", "error", err)
			return nil, fmt.Errorf("%w: %w", ErrValidation, err)
		}
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

	for _, tr := range req.Transactions {
		if err := s.validator.Struct(tr); err != nil {
			s.logger.Warn("service: validation failed", "error", err)
			return nil, fmt.Errorf("%w: %w", ErrValidation, err)
		}
	}

	out, err := s.launcher.RunAdviceModel(req)
	if err != nil {
		s.logger.Error("service: advice request processing failed", "error", err)
		return nil, fmt.Errorf("%w: %w", ErrMl, err)
	}

	return out, nil
}

// ScanReceipt — прокидываем путь к файлу чека в ML-сервис
func (s *MlApiService) ScanReceipt(ctx context.Context, path models.ReceiptFilePath) (*models.ReceiptScanResponse, error) {
	s.logger.Info("service: scan receipt request", "path", path)

	if path == "" {
		s.logger.Warn("service: empty receipt path")
		return nil, fmt.Errorf("%w: empty receipt path", ErrValidation)
	}

	out, err := s.launcher.RunReceiptScan(path) // ВАЖНО: БЕЗ string(path)
	if err != nil {
		s.logger.Error("service: receipt scan processing failed", "error", err)
		return nil, fmt.Errorf("%w: %w", ErrMl, err)
	}

	return out, nil
}
