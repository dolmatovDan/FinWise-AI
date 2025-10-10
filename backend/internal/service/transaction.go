package service

import (
	"context"
	"fmt"
	"log/slog"

	"github.com/dolmatovDan/FinWise-AI/backend/internal/models"
	"github.com/dolmatovDan/FinWise-AI/backend/internal/storage"
	"github.com/go-playground/validator/v10"
	"github.com/google/uuid"
)

// TransactionService handles transaction business logic
type TransactionService struct {
	repo      storage.TransactionRepository
	logger    *slog.Logger
	validator *validator.Validate
}

// NewTransactionService creates a new transaction service instance
func NewTransactionService(repo storage.TransactionRepository, logger *slog.Logger) *TransactionService {
	return &TransactionService{
		repo:      repo,
		logger:    logger,
		validator: validator.New(),
	}
}

// Create creates a new transaction with validation
func (s *TransactionService) Create(ctx context.Context, req *models.CreateTransactionRequest) (*models.Transaction, error) {
	s.logger.Info("service: creating transaction", "user_id", req.UserID, "type", req.Type)

	// Validate request
	if err := s.validator.Struct(req); err != nil {
		s.logger.Warn("service: validation failed", "error", err)
		return nil, fmt.Errorf("%w: %w", ErrValidation, err)
	}

	// Call storage layer
	transaction, err := s.repo.Create(ctx, req)
	if err != nil {
		s.logger.Error("service: failed to create transaction", "error", err)
		return nil, fmt.Errorf("failed to create transaction: %w", err)
	}

	s.logger.Info("service: transaction created successfully", "id", transaction.ID)
	return transaction, nil
}

// GetByID retrieves a transaction by ID
func (s *TransactionService) GetByID(ctx context.Context, id uuid.UUID) (*models.Transaction, error) {
	s.logger.Info("service: fetching transaction", "id", id)

	transaction, err := s.repo.GetByID(ctx, id)
	if err != nil {
		s.logger.Error("service: failed to get transaction", "id", id, "error", err)
		return nil, fmt.Errorf("failed to get transaction: %w", err)
	}

	return transaction, nil
}

// List retrieves transactions with filtering and pagination
func (s *TransactionService) List(ctx context.Context, filter *models.TransactionFilter) (*models.TransactionListResponse, error) {
	s.logger.Info("service: listing transactions", "filter", filter)

	// Validate filter
	if err := s.validator.Struct(filter); err != nil {
		s.logger.Warn("service: filter validation failed", "error", err)
		return nil, fmt.Errorf("%w: %w", ErrValidation, err)
	}

	response, err := s.repo.List(ctx, filter)
	if err != nil {
		s.logger.Error("service: failed to list transactions", "error", err)
		return nil, fmt.Errorf("failed to list transactions: %w", err)
	}

	s.logger.Info("service: transactions listed successfully", "count", len(response.Transactions))
	return response, nil
}

// Update updates a transaction with validation
func (s *TransactionService) Update(ctx context.Context, id uuid.UUID, req *models.UpdateTransactionRequest) (*models.Transaction, error) {
	s.logger.Info("service: updating transaction", "id", id)

	// Validate request
	if err := s.validator.Struct(req); err != nil {
		s.logger.Warn("service: validation failed", "error", err)
		return nil, fmt.Errorf("%w: %w", ErrValidation, err)
	}

	transaction, err := s.repo.Update(ctx, id, req)
	if err != nil {
		s.logger.Error("service: failed to update transaction", "id", id, "error", err)
		return nil, fmt.Errorf("failed to update transaction: %w", err)
	}

	s.logger.Info("service: transaction updated successfully", "id", transaction.ID)
	return transaction, nil
}

// Delete deletes a transaction
func (s *TransactionService) Delete(ctx context.Context, id uuid.UUID) error {
	s.logger.Info("service: deleting transaction", "id", id)

	if err := s.repo.Delete(ctx, id); err != nil {
		s.logger.Error("service: failed to delete transaction", "id", id, "error", err)
		return fmt.Errorf("failed to delete transaction: %w", err)
	}

	s.logger.Info("service: transaction deleted successfully", "id", id)
	return nil
}
