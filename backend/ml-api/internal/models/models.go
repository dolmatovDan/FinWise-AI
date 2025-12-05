package models

import (
	"time"

	"github.com/shopspring/decimal"
)

type Transaction struct {
	Date        time.Time       `json:"date" validate:"required"`
	Amount      decimal.Decimal `json:"amount" validate:"required"`
	Type        string          `json:"type" validate:"required,oneof=income expense"`
	Category    string          `json:"category"`
	Description string          `json:"description" validate:"omitempty,max=500"`
}

// Request a forecast on spending based on data
type ForecastRequest struct {
	Granularity  string        `json:"granularity" validate:"required,oneof=month year"`
	Steps        int64         `json:"steps" validate:"required,gt=0"`
	Model        string        `json:"model"` // TODO: model possible values
	Transactions []Transaction `json:"transactions" validate:"required,min=1"`
}

type ForecastResponse struct {
	PeriodEnd       []time.Time       `json:"period_end"`
	IncomeForecast  []decimal.Decimal `json:"income_forecast"`
	ExpenseForecast []decimal.Decimal `json:"expense_forecast"`
}

// Request an advice on how to spend less or anything from a ML model
type AdviceRequest struct {
	Question     string        `json:"question"`
	Transactions []Transaction `json:"transactions"`
}

type AdviceResponse struct {
	Advice string `json:"advice"`
}

// Scan receipt via computer vision and output its sum
type ReceiptFilePath string

type ReceiptScanResponse struct {
	Total       decimal.Decimal `json:"total"`
	Category    string          `json:"category_name,omitempty"`
	Description string          `json:"description,omitempty"`
}
