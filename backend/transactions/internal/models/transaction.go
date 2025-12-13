package models

import (
	"time"

	"github.com/google/uuid"
	"github.com/shopspring/decimal"
)

// TransactionType represents the type of transaction
type TransactionType string

const (
	TransactionTypeIncome  TransactionType = "income"
	TransactionTypeExpense TransactionType = "expense"
)

// Transaction represents a financial transaction
type Transaction struct {
	ID          uuid.UUID       `json:"id" db:"id"`
	UserID      int64           `json:"user_id" db:"user_id"` // For future JWT authentication
	Amount      decimal.Decimal `json:"amount" db:"amount"`
	Category    string          `json:"category" db:"category"`
	Description string          `json:"description" db:"description"`
	Type        TransactionType `json:"type" db:"type"`
	CreatedAt   time.Time       `json:"created_at" db:"created_at"`
	UpdatedAt   time.Time       `json:"updated_at" db:"updated_at"`
}

// CreateTransactionRequest represents request to create a new transaction
type CreateTransactionRequest struct {
	UserID      int64           `json:"-" validate:"required,gt=0"`
	Amount      decimal.Decimal `json:"amount" binding:"required"`
	Category    string          `json:"category" binding:"required" validate:"required,min=1,max=100"`
	Description string          `json:"description" validate:"omitempty,max=500"`
	Type        TransactionType `json:"type" binding:"required,oneof=income expense" validate:"required,oneof=income expense"`
}

// UpdateTransactionRequest represents request to update a transaction
type UpdateTransactionRequest struct {
	Amount      *decimal.Decimal `json:"amount,omitempty"`
	Category    *string          `json:"category,omitempty" validate:"omitempty,min=1,max=100"`
	Description *string          `json:"description,omitempty" validate:"omitempty,max=500"`
	Type        *TransactionType `json:"type,omitempty" validate:"omitempty,oneof=income expense"`
}

// TransactionListResponse represents paginated list of transactions
type TransactionListResponse struct {
	Transactions []Transaction `json:"transactions"`
	Total        int64         `json:"total"`
	Page         int           `json:"page"`
	PageSize     int           `json:"page_size"`
}

// TransactionFilter represents filters for querying transactions
type TransactionFilter struct {
	UserID   int64            `form:"user_id" validate:"omitempty,gte=0"`
	Type     *TransactionType `form:"type" validate:"omitempty,oneof=income expense"`
	Category *string          `form:"category" validate:"omitempty,min=1,max=100"`
	Page     int              `form:"page" validate:"omitempty,gte=1"`
	PageSize int              `form:"page_size" validate:"omitempty,gte=1,lte=100"`
}

// ProfitRequest represents request to calculate profit over time periods
type ProfitRequest struct {
	StartDate time.Time `json:"start_date" binding:"required" validate:"required"`
	EndDate   time.Time `json:"end_date" binding:"required" validate:"required,gtfield=StartDate"`
	Interval  int64     `json:"interval" binding:"required" validate:"required,gt=0,lte=31536000"` // max 1 year in seconds
}

// ProfitDataPoint represents a single profit data point for a specific timestamp
type ProfitDataPoint struct {
	Timestamp time.Time       `json:"timestamp"`
	Profit    decimal.Decimal `json:"profit"`
}

// ProfitResponse represents response with profit data over time
type ProfitResponse struct {
	Data []ProfitDataPoint `json:"data"`
}
