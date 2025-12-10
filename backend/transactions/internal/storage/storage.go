package storage

import (
	"context"
	"time"

	"github.com/dolmatovDan/FinWise-AI/backend/transactions/internal/models"
	"github.com/google/uuid"
)

// TransactionRepository defines the interface for transaction storage operations
type TransactionRepository interface {
	// Create creates a new transaction
	Create(ctx context.Context, req *models.CreateTransactionRequest) (*models.Transaction, error)

	// GetByID retrieves a transaction by ID
	GetByID(ctx context.Context, id uuid.UUID) (*models.Transaction, error)

	// List retrieves transactions with filtering and pagination
	List(ctx context.Context, filter *models.TransactionFilter) (*models.TransactionListResponse, error)

	// Update updates a transaction
	Update(ctx context.Context, id uuid.UUID, req *models.UpdateTransactionRequest) (*models.Transaction, error)

	// Delete deletes a transaction
	Delete(ctx context.Context, id uuid.UUID) error

	// GetProfitByPeriods calculates profit (income - expense) aggregated by time periods
	GetProfitByPeriods(ctx context.Context, userID int64, startDate, endDate time.Time, intervalSeconds int64) ([]models.ProfitDataPoint, error)
}
