package storage

import (
	"context"
	"time"

	"github.com/dolmatovDan/FinWise-AI/backend/transactions/internal/models"
	"github.com/google/uuid"
	"github.com/stretchr/testify/mock"
)

// A fake TransactionRepository that returns placeholder values

type MockRepository struct { 
	mock.Mock
}

func (r *MockRepository) Create(ctx context.Context, req *models.CreateTransactionRequest) (*models.Transaction, error) {
	args := r.Called(ctx, req)
	err := args.Error(1)
	if tr, ok := args.Get(0).(*models.Transaction); ok {
  		return tr, err
	} else {
		return nil, err
	}
}

func (r *MockRepository) GetByID(ctx context.Context, id uuid.UUID) (*models.Transaction, error) {
	args := r.Called(ctx, id)
	err := args.Error(1)
	if tr, ok := args.Get(0).(*models.Transaction); ok {
  		return tr, err
	} else {
		return nil, err
	}
}

func (r *MockRepository) List(ctx context.Context, filter *models.TransactionFilter) (*models.TransactionListResponse, error) {
	args := r.Called(ctx, filter)
	err := args.Error(1)
	if trl, ok := args.Get(0).(*models.TransactionListResponse); ok {
  		return trl, err
	} else {
		return nil, err
	}
}

func (r *MockRepository) Update(ctx context.Context, id uuid.UUID, req *models.UpdateTransactionRequest) (*models.Transaction, error) {
	args := r.Called(ctx, id, req)
	err := args.Error(1)
	if tr, ok := args.Get(0).(*models.Transaction); ok {
  		return tr, err
	} else {
		return nil, err
	}
}

func (r *MockRepository) Delete(ctx context.Context, id uuid.UUID) error {
	args := r.Called(ctx, id)
	return args.Error(0)
}

func (r *MockRepository) GetProfitByPeriods(ctx context.Context, userID int64, startDate, endDate time.Time, intervalSeconds int64) ([]models.ProfitDataPoint, error) {
	args := r.Called(ctx, userID, startDate, endDate, intervalSeconds)
	err := args.Error(1)
	if data, ok := args.Get(0).([]models.ProfitDataPoint); ok {
		return data, err
	} else {
		return nil, err
	}
}
