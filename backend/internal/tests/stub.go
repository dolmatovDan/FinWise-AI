package tests

import (
	"context"
	"time"

	"github.com/dolmatovDan/FinWise-AI/backend/internal/models"
	"github.com/google/uuid"
)

type StubRepository struct { }

var fake_transaction models.Transaction = models.Transaction{
	ID: uuid.New(),
	UserID: 1,
	Category: "sonic",
	Description: "tails",
	Type: models.TransactionTypeIncome,
	CreatedAt: time.Now(),
	UpdatedAt: time.Now(),
}

func (r *StubRepository) Create(ctx context.Context, req *models.CreateTransactionRequest) (*models.Transaction, error) {
	return &fake_transaction, nil
}

func (r *StubRepository) GetByID(ctx context.Context, id uuid.UUID) (*models.Transaction, error) {
	return &fake_transaction, nil
}

func (r *StubRepository) List(ctx context.Context, filter *models.TransactionFilter) (*models.TransactionListResponse, error) {
	return &models.TransactionListResponse{
		Transactions: []models.Transaction{fake_transaction},
		Total: 1,
		Page: 1,
		PageSize: 1,
	}, nil
}

func (r *StubRepository) Update(ctx context.Context, id uuid.UUID, req *models.UpdateTransactionRequest) (*models.Transaction, error) {
	return &fake_transaction, nil
}

func (r *StubRepository) Delete(ctx context.Context, id uuid.UUID) error {
	return nil
}
