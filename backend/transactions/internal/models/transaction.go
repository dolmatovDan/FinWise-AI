package models

import (
	"time"

	optional "github.com/denpa16/optional-go-type"
	"github.com/google/uuid"
	"github.com/shopspring/decimal"
)

// TransactionType represents the type of transaction
type TransactionType string

const (
	TransactionTypeIncome  TransactionType = "income"
	TransactionTypeExpense TransactionType = "expense"
)

// Category represents a transaction category
type Category struct {
	ID          int64  `json:"id" db:"id"`
	Name        string `json:"name" db:"name"`
	Description string `json:"description" db:"description"`
}

// Transaction represents a financial transaction
type Transaction struct {
	ID          uuid.UUID       `json:"id" db:"id"`
	UserID      int64           `json:"user_id" db:"user_id"` // For future JWT authentication
	Amount      decimal.Decimal `json:"amount" db:"amount"`
	CategoryID  int64           `json:"category_id" db:"category_id"`
	Description string          `json:"description" db:"description"`
	Type        TransactionType `json:"type" db:"type"`
	CreatedAt   time.Time       `json:"created_at" db:"created_at"`
	UpdatedAt   time.Time       `json:"updated_at" db:"updated_at"`
}

// CreateTransactionRequest represents request to create a new transaction
type CreateTransactionRequest struct {
	UserID      int64           `json:"user_id" binding:"required" validate:"required,gt=0"`
	Amount      decimal.Decimal `json:"amount" binding:"required" validate:"required,gt=0"`
	CategoryID  int64           `json:"category_id" binding:"required" validate:"required"`
	Description string          `json:"description" validate:"omitempty,max=500"`
	Type        TransactionType `json:"type" binding:"required,oneof=income expense" validate:"required,oneof=income expense"`
}

// UpdateTransactionRequest represents request to update a transaction
type UpdateTransactionRequest struct {
	Amount      optional.OptionalType[decimal.Decimal] `json:"amount,omitempty" validate:"omitempty,gt=0"`
	CategoryID  optional.OptionalInt64                 `json:"category_id,omitempty" validate:"omitempty"`
	Description optional.OptionalString                `json:"description,omitempty" validate:"omitempty,max=500"`
	Type        optional.OptionalType[TransactionType] `json:"type,omitempty" validate:"omitempty,oneof=income expense"`
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
	UserID     optional.OptionalInt64                 `form:"user_id" validate:"omitempty,gte=1"`
	Type       optional.OptionalType[TransactionType] `form:"type" validate:"omitempty,oneof=income expense"`
	CategoryID optional.OptionalInt64                 `form:"category_id" validate:"omitempty"`
	Page       optional.OptionalInt                   `form:"page" validate:"omitempty,gte=1"`
	PageSize   optional.OptionalInt                   `form:"page_size" validate:"omitempty,gte=1,lte=100"`
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
