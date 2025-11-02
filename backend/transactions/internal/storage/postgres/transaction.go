package postgres

import (
	"context"
	"errors"
	"fmt"
	"strings"

	"github.com/dolmatovDan/FinWise-AI/backend/transactions/internal/models"
	"github.com/dolmatovDan/FinWise-AI/backend/transactions/internal/storage"
	"github.com/google/uuid"
	"github.com/jackc/pgx/v5"
)

// TransactionStorage handles transaction database operations
type TransactionStorage struct {
	storage *Storage
}

// Compile-time check to ensure TransactionStorage implements storage.TransactionRepository
var _ storage.TransactionRepository = (*TransactionStorage)(nil)

// NewTransactionStorage creates a new transaction storage instance
func NewTransactionStorage(storage *Storage) *TransactionStorage {
	return &TransactionStorage{
		storage: storage,
	}
}

// Create creates a new transaction
func (ts *TransactionStorage) Create(ctx context.Context, req *models.CreateTransactionRequest) (*models.Transaction, error) {
	ts.storage.logger.Info("creating new transaction", "user_id", req.UserID, "type", req.Type)

	query := `
		INSERT INTO transaction (user_id, amount, category, description, type)
		VALUES ($1, $2, $3, $4, $5)
		RETURNING id, user_id, amount, category, description, type, created_at, updated_at
	`

	var transaction models.Transaction
	err := ts.storage.pool.QueryRow(ctx, query,
		req.UserID,
		req.Amount,
		req.Category,
		req.Description,
		req.Type,
	).Scan(
		&transaction.ID,
		&transaction.UserID,
		&transaction.Amount,
		&transaction.Category,
		&transaction.Description,
		&transaction.Type,
		&transaction.CreatedAt,
		&transaction.UpdatedAt,
	)

	if err != nil {
		ts.storage.logger.Error("failed to create transaction", "error", err)
		return nil, fmt.Errorf("failed to create transaction: %w", err)
	}

	ts.storage.logger.Info("transaction created successfully", "id", transaction.ID)
	return &transaction, nil
}

// GetByID retrieves a transaction by ID
func (ts *TransactionStorage) GetByID(ctx context.Context, id uuid.UUID) (*models.Transaction, error) {
	ts.storage.logger.Info("fetching transaction by id", "id", id)

	query := `
		SELECT id, user_id, amount, category, description, type, created_at, updated_at
		FROM transaction
		WHERE id = $1
	`

	var transaction models.Transaction
	err := ts.storage.pool.QueryRow(ctx, query, id).Scan(
		&transaction.ID,
		&transaction.UserID,
		&transaction.Amount,
		&transaction.Category,
		&transaction.Description,
		&transaction.Type,
		&transaction.CreatedAt,
		&transaction.UpdatedAt,
	)

	if err != nil {
		if errors.Is(err, pgx.ErrNoRows) {
			ts.storage.logger.Warn("transaction not found", "id", id)
			return nil, fmt.Errorf("transaction not found")
		}
		ts.storage.logger.Error("failed to get transaction", "error", err)
		return nil, fmt.Errorf("failed to get transaction: %w", err)
	}

	return &transaction, nil
}

// List retrieves transactions with filtering and pagination
func (ts *TransactionStorage) List(ctx context.Context, filter *models.TransactionFilter) (*models.TransactionListResponse, error) {
	ts.storage.logger.Info("fetching transactions list", "filter", filter)

	// Set default values
	if filter.Page == 0 {
		filter.Page = 1
	}
	if filter.PageSize == 0 {
		filter.PageSize = 10
	}

	// Build WHERE clause
	whereConditions := []string{"1=1"}
	args := []interface{}{}
	argCounter := 1

	if filter.UserID != 0 {
		whereConditions = append(whereConditions, fmt.Sprintf("user_id = $%d", argCounter))
		args = append(args, filter.UserID)
		argCounter++
	}

	if filter.Type != nil {
		whereConditions = append(whereConditions, fmt.Sprintf("type = $%d", argCounter))
		args = append(args, *filter.Type)
		argCounter++
	}

	if filter.Category != nil {
		whereConditions = append(whereConditions, fmt.Sprintf("category = $%d", argCounter))
		args = append(args, *filter.Category)
		argCounter++
	}

	whereClause := strings.Join(whereConditions, " AND ")

	// Get total count
	countQuery := fmt.Sprintf("SELECT COUNT(*) FROM transaction WHERE %s", whereClause)
	var total int64
	err := ts.storage.pool.QueryRow(ctx, countQuery, args...).Scan(&total)
	if err != nil {
		ts.storage.logger.Error("failed to count transactions", "error", err)
		return nil, fmt.Errorf("failed to count transactions: %w", err)
	}

	// Get transactions
	offset := (filter.Page - 1) * filter.PageSize
	args = append(args, filter.PageSize, offset)

	query := fmt.Sprintf(`
		SELECT id, user_id, amount, category, description, type, created_at, updated_at
		FROM transaction
		WHERE %s
		ORDER BY created_at DESC
		LIMIT $%d OFFSET $%d
	`, whereClause, argCounter, argCounter+1)

	rows, err := ts.storage.pool.Query(ctx, query, args...)
	if err != nil {
		ts.storage.logger.Error("failed to query transactions", "error", err)
		return nil, fmt.Errorf("failed to query transactions: %w", err)
	}
	defer rows.Close()

	transactions := []models.Transaction{}
	for rows.Next() {
		var transaction models.Transaction
		err := rows.Scan(
			&transaction.ID,
			&transaction.UserID,
			&transaction.Amount,
			&transaction.Category,
			&transaction.Description,
			&transaction.Type,
			&transaction.CreatedAt,
			&transaction.UpdatedAt,
		)
		if err != nil {
			ts.storage.logger.Error("failed to scan transaction", "error", err)
			return nil, fmt.Errorf("failed to scan transaction: %w", err)
		}
		transactions = append(transactions, transaction)
	}

	if err := rows.Err(); err != nil {
		ts.storage.logger.Error("error iterating transactions", "error", err)
		return nil, fmt.Errorf("error iterating transactions: %w", err)
	}

	ts.storage.logger.Info("transactions fetched successfully", "count", len(transactions), "total", total)

	return &models.TransactionListResponse{
		Transactions: transactions,
		Total:        total,
		Page:         filter.Page,
		PageSize:     filter.PageSize,
	}, nil
}

// Update updates a transaction
func (ts *TransactionStorage) Update(ctx context.Context, id uuid.UUID, req *models.UpdateTransactionRequest) (*models.Transaction, error) {
	ts.storage.logger.Info("updating transaction", "id", id)

	// Build SET clause dynamically based on provided fields
	setClauses := []string{}
	args := []interface{}{}
	argCounter := 1

	if req.Amount != nil {
		setClauses = append(setClauses, fmt.Sprintf("amount = $%d", argCounter))
		args = append(args, *req.Amount)
		argCounter++
	}

	if req.Category != nil {
		setClauses = append(setClauses, fmt.Sprintf("category = $%d", argCounter))
		args = append(args, *req.Category)
		argCounter++
	}

	if req.Description != nil {
		setClauses = append(setClauses, fmt.Sprintf("description = $%d", argCounter))
		args = append(args, *req.Description)
		argCounter++
	}

	if req.Type != nil {
		setClauses = append(setClauses, fmt.Sprintf("type = $%d", argCounter))
		args = append(args, *req.Type)
		argCounter++
	}

	if len(setClauses) == 0 {
		ts.storage.logger.Warn("no fields to update", "id", id)
		return ts.GetByID(ctx, id)
	}

	args = append(args, id)
	query := fmt.Sprintf(`
		UPDATE transaction
		SET %s
		WHERE id = $%d
		RETURNING id, user_id, amount, category, description, type, created_at, updated_at
	`, strings.Join(setClauses, ", "), argCounter)

	var transaction models.Transaction
	err := ts.storage.pool.QueryRow(ctx, query, args...).Scan(
		&transaction.ID,
		&transaction.UserID,
		&transaction.Amount,
		&transaction.Category,
		&transaction.Description,
		&transaction.Type,
		&transaction.CreatedAt,
		&transaction.UpdatedAt,
	)

	if err != nil {
		if errors.Is(err, pgx.ErrNoRows) {
			ts.storage.logger.Warn("transaction not found", "id", id)
			return nil, fmt.Errorf("transaction not found")
		}
		ts.storage.logger.Error("failed to update transaction", "error", err)
		return nil, fmt.Errorf("failed to update transaction: %w", err)
	}

	ts.storage.logger.Info("transaction updated successfully", "id", transaction.ID)
	return &transaction, nil
}

// Delete deletes a transaction
func (ts *TransactionStorage) Delete(ctx context.Context, id uuid.UUID) error {
	ts.storage.logger.Info("deleting transaction", "id", id)

	query := `DELETE FROM transaction WHERE id = $1`

	result, err := ts.storage.pool.Exec(ctx, query, id)
	if err != nil {
		ts.storage.logger.Error("failed to delete transaction", "error", err)
		return fmt.Errorf("failed to delete transaction: %w", err)
	}

	if result.RowsAffected() == 0 {
		ts.storage.logger.Warn("transaction not found", "id", id)
		return fmt.Errorf("transaction not found")
	}

	ts.storage.logger.Info("transaction deleted successfully", "id", id)
	return nil
}
