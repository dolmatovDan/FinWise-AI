package postgres

import (
	"context"
	"errors"
	"fmt"
	"strings"
	"time"

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

	if _, err := getCategoryById(ts, ctx, req.CategoryID); err != nil {
		ts.storage.logger.Error("failed to create transaction", "error", err)
		return nil, fmt.Errorf("failed to create transaction: %w", err)
	}

	query := `
		INSERT INTO transaction (user_id, amount, category_id, description, type)
		VALUES ($1, $2, $3, $4, $5)
		RETURNING id, user_id, amount, category_id, description, type, created_at, updated_at
	`

	var transaction models.Transaction
	err := ts.storage.pool.QueryRow(ctx, query,
		req.UserID,
		req.Amount,
		req.CategoryID,
		req.Description,
		req.Type,
	).Scan(
		&transaction.ID,
		&transaction.UserID,
		&transaction.Amount,
		&transaction.CategoryID,
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
		SELECT id, user_id, amount, category_id, description, type, created_at, updated_at
		FROM transaction
		WHERE id = $1
	`

	var transaction models.Transaction
	err := ts.storage.pool.QueryRow(ctx, query, id).Scan(
		&transaction.ID,
		&transaction.UserID,
		&transaction.Amount,
		&transaction.CategoryID,
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

	if filter.CategoryID != nil {
		if _, err := getCategoryById(ts, ctx, *filter.CategoryID); err != nil {
			ts.storage.logger.Error("failed to fetch transactions list", "error", err)
			return nil, fmt.Errorf("failed to fetch transactions list: %w", err)
		}
	}

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

	if filter.CategoryID != nil {
		whereConditions = append(whereConditions, fmt.Sprintf("category_id = $%d", argCounter))
		args = append(args, *filter.CategoryID)
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
		SELECT id, user_id, amount, category_id, description, type, created_at, updated_at
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
			&transaction.CategoryID,
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

	if req.CategoryID != nil {
		if _, err := getCategoryById(ts, ctx, *req.CategoryID); err != nil {
			ts.storage.logger.Error("failed to create transaction", "error", err)
			return nil, fmt.Errorf("failed to create transaction: %w", err)
		}
	}

	// Build SET clause dynamically based on provided fields
	setClauses := []string{}
	args := []interface{}{}
	argCounter := 1

	if req.Amount != nil {
		setClauses = append(setClauses, fmt.Sprintf("amount = $%d", argCounter))
		args = append(args, *req.Amount)
		argCounter++
	}

	if req.CategoryID != nil {
		setClauses = append(setClauses, fmt.Sprintf("category_id = $%d", argCounter))
		args = append(args, *req.CategoryID)
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
		RETURNING id, user_id, amount, category_id, description, type, created_at, updated_at
	`, strings.Join(setClauses, ", "), argCounter)

	var transaction models.Transaction
	err := ts.storage.pool.QueryRow(ctx, query, args...).Scan(
		&transaction.ID,
		&transaction.UserID,
		&transaction.Amount,
		&transaction.CategoryID,
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

func (ts *TransactionStorage) GetCategories(ctx context.Context) (*[]models.Category, error) {
	ts.storage.logger.Info("fetching category list")

	query := `SELECT * FROM category`

	rows, err := ts.storage.pool.Query(ctx, query)
	if err != nil {
		ts.storage.logger.Error("failed to query categories: %w", "error", err)
		return nil, fmt.Errorf("failed to query categories: %w", err)
	}
	defer rows.Close()

	categories := []models.Category{}
	for rows.Next() {
		var category models.Category
		err := rows.Scan(
			&category.ID,
			&category.Name,
			&category.Description,
		)
		if err != nil {
			ts.storage.logger.Error("failed to scan category", "error", err)
			return nil, fmt.Errorf("failed to scan category: %w", err)
		}
		categories = append(categories, category)
	}

	if err := rows.Err(); err != nil {
		ts.storage.logger.Error("error iterating categories", "error", err)
		return nil, fmt.Errorf("error iterating categories: %w", err)
	}

	ts.storage.logger.Info("categories fetched successfully", "count", len(categories))

	return &categories, nil
}

// GetProfitByPeriods calculates profit (income - expense) aggregated by time periods
func (ts *TransactionStorage) GetProfitByPeriods(ctx context.Context, userID int64, startDate, endDate time.Time, intervalSeconds int64) ([]models.ProfitDataPoint, error) {
	ts.storage.logger.Info("calculating profit by periods", "user_id", userID, "start_date", startDate, "end_date", endDate, "interval", intervalSeconds)

	query := `
		WITH time_series AS (
			SELECT generate_series(
				$2::timestamptz,
				$3::timestamptz,
				make_interval(secs => $4::int)
			) AS period_start
		),
		aggregated AS (
			SELECT
				ts.period_start,
				COALESCE(
					SUM(CASE WHEN t.type = 'income' THEN t.amount ELSE 0 END) -
					SUM(CASE WHEN t.type = 'expense' THEN t.amount ELSE 0 END),
					0
				) AS profit
			FROM time_series ts
			LEFT JOIN transaction t ON
				t.user_id = $1
				AND t.created_at >= ts.period_start
				AND t.created_at < ts.period_start + make_interval(secs => $4::int)
			GROUP BY ts.period_start
			ORDER BY ts.period_start
		)
		SELECT period_start, profit FROM aggregated
	`

	rows, err := ts.storage.pool.Query(ctx, query, userID, startDate, endDate, intervalSeconds)
	if err != nil {
		ts.storage.logger.Error("failed to calculate profit by periods", "error", err)
		return nil, fmt.Errorf("failed to calculate profit by periods: %w", err)
	}
	defer rows.Close()

	var dataPoints []models.ProfitDataPoint
	for rows.Next() {
		var point models.ProfitDataPoint
		err := rows.Scan(&point.Timestamp, &point.Profit)
		if err != nil {
			ts.storage.logger.Error("failed to scan profit data point", "error", err)
			return nil, fmt.Errorf("failed to scan profit data point: %w", err)
		}
		dataPoints = append(dataPoints, point)
	}

	if err := rows.Err(); err != nil {
		ts.storage.logger.Error("error iterating profit data points", "error", err)
		return nil, fmt.Errorf("error iterating profit data points: %w", err)
	}

	ts.storage.logger.Info("profit calculated successfully", "data_points_count", len(dataPoints))
	return dataPoints, nil
}

func getCategoryById(ts *TransactionStorage, ctx context.Context, categoryID int64) (*models.Category, error) {
	ts.storage.logger.Info("attempting to fetch category by ID", "category_id", categoryID)

	query := `SELECT * FROM category WHERE id = $1`

	rows, err := ts.storage.pool.Query(ctx, query, categoryID)
	if err != nil {
		ts.storage.logger.Error("failed to fetch category", "error", err)
		return nil, fmt.Errorf("failed to fetch category: %w", err)
	}
	defer rows.Close()

	var category models.Category
	if rows.Next() {
		if err := rows.Scan(&category.ID, &category.Name, &category.Description); err != nil {
			ts.storage.logger.Error("failed to scan category", "error", err)
			return nil, fmt.Errorf("failed to scan category: %w", err)
		}
	} else {
		ts.storage.logger.Error("category not found", "category_id", categoryID)
		return nil, fmt.Errorf("category not found: %d", categoryID)
	}

	ts.storage.logger.Info("category fetched successfully", "category_id", categoryID)
	return &category, nil
}
