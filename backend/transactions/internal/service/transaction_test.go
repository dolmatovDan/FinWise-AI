package service_test

import (
	"context"
	"log/slog"
	"testing"
	"time"

	optional "github.com/denpa16/optional-go-type"
	"github.com/dolmatovDan/FinWise-AI/backend/transactions/internal/models"
	"github.com/dolmatovDan/FinWise-AI/backend/transactions/internal/service"
	"github.com/dolmatovDan/FinWise-AI/backend/transactions/internal/storage"
	"github.com/google/uuid"
	"github.com/shopspring/decimal"
	"github.com/stretchr/testify/mock"
	"github.com/stretchr/testify/require"
)

func TestServiceValidator(t *testing.T) {
	dec1, _ := decimal.NewFromString("12.34")
	dec2, _ := decimal.NewFromString("56.78")
	decNegative, _ := decimal.NewFromString("-12.34")
	decTooLarge, _ := decimal.NewFromString("123456789123456789123456.512345678")

	fakeTransaction := models.Transaction{
		ID:          uuid.New(),
		UserID:      1,
		Amount:      dec1,
		CategoryID:  1,
		Description: "tails",
		Type:        models.TransactionTypeIncome,
		CreatedAt:   time.Now(),
		UpdatedAt:   time.Now(),
	}

	var repo *storage.MockRepository = new(storage.MockRepository)
	repo.On("Create", mock.Anything, mock.Anything).Return(&fakeTransaction, nil)
	repo.On("Update", mock.Anything, mock.Anything, mock.Anything).Return(&fakeTransaction, nil)
	repo.On("List", mock.Anything, mock.Anything).Return(&models.TransactionListResponse{
		Transactions: []models.Transaction{fakeTransaction},
		Total:        1,
		Page:         1,
		PageSize:     1,
	}, nil)
	repo.On("GetCategories").Return(&[]models.Category{
		{ID: 1, Name: "Packs", Description: "qwertyuiop"},
		{ID: 2, Name: "Snacks", Description: "asdfghjkl"},
		{ID: 3, Name: "Tracks", Description: "zxcvbnm"},
	}, nil)

	var logger *slog.Logger = slog.Default()
	serv := service.NewTransactionService(repo, logger)

	// TODO: get rid of string pointers in service implementation
	strTest := "sonic"
	typeExpense := models.TransactionTypeExpense
	typeIncome := models.TransactionTypeIncome
	var typeBad models.TransactionType = "knuckles"

	t.Run("TestValidRequest", func(t *testing.T) {
		_, err := serv.Create(context.Background(), &models.CreateTransactionRequest{
			UserID:      1,
			Amount:      dec1,
			CategoryID:  1,
			Description: "tails",
			Type:        "income",
		})
		require.Nil(t, err, "valid create request #1 returns an error")
		_, err = serv.Create(context.Background(), &models.CreateTransactionRequest{
			UserID:      2,
			Amount:      dec2,
			CategoryID:  2,
			Description: "",
			Type:        "expense",
		})
		require.Nil(t, err, "valid create request #2 returns an error")
		_, err = serv.Update(context.Background(), uuid.New(), &models.UpdateTransactionRequest{
			Amount:      newOptionalCustomType[decimal.Decimal](dec1),
			Description: optional.NewOptionalString(&strTest),
			Type:        newOptionalCustomType[models.TransactionType](typeIncome),
		})
		require.Nil(t, err, "valid update request #1 returns an error")
		_, err = serv.Update(context.Background(), uuid.New(), &models.UpdateTransactionRequest{
			Amount:     newOptionalCustomType[decimal.Decimal](dec2),
			CategoryID: newOptionalConstInt64(3),
			Type:       newOptionalCustomType[models.TransactionType](typeExpense),
		})
		require.Nil(t, err, "valid update request #2 returns an error")
		_, err = serv.Update(context.Background(), uuid.New(), &models.UpdateTransactionRequest{})
		require.Nil(t, err, "valid update request #3 returns an error")
	})

	t.Run("TestInvalidUserId", func(t *testing.T) {
		_, err := serv.Create(context.Background(), &models.CreateTransactionRequest{
			UserID:      -4,
			Amount:      dec1,
			CategoryID:  1,
			Description: "tails",
			Type:        "income",
		})
		require.NotNil(t, err, "request with negative user ID passes successfully")
		_, err = serv.Create(context.Background(), &models.CreateTransactionRequest{
			UserID:      0,
			Amount:      dec1,
			CategoryID:  2,
			Description: "tails",
			Type:        "expense",
		})
		require.NotNil(t, err, "request with zero user ID passes successfully")
	})

	t.Run("TestInvalidAmount", func(t *testing.T) {
		_, err := serv.Create(context.Background(), &models.CreateTransactionRequest{
			UserID:      1,
			Amount:      decNegative,
			CategoryID:  1,
			Description: "tails",
			Type:        "income",
		})
		require.NotNil(t, err, "create request with negative amount passes successfully")
		_, err = serv.Create(context.Background(), &models.CreateTransactionRequest{
			UserID:      2,
			Amount:      decTooLarge,
			CategoryID:  2,
			Description: "tails",
			Type:        "expense",
		})
		require.NotNil(t, err, "create request with too large amount passes successfully")
		_, err = serv.Update(context.Background(), uuid.New(), &models.UpdateTransactionRequest{
			Amount:      newOptionalCustomType[decimal.Decimal](decNegative),
			CategoryID:  newOptionalConstInt64(1),
			Description: optional.NewOptionalString(&strTest),
			Type:        newOptionalCustomType[models.TransactionType](typeIncome),
		})
		require.NotNil(t, err, "update request with negative amount passes successfully")
		_, err = serv.Update(context.Background(), uuid.New(), &models.UpdateTransactionRequest{
			Amount:      newOptionalCustomType[decimal.Decimal](decTooLarge),
			CategoryID:  newOptionalConstInt64(2),
			Description: optional.NewOptionalString(&strTest),
			Type:        newOptionalCustomType[models.TransactionType](typeExpense),
		})
		require.NotNil(t, err, "update request with too large amount passes successfully")
	})

	t.Run("TestInvalidType", func(t *testing.T) {
		_, err := serv.Create(context.Background(), &models.CreateTransactionRequest{
			UserID:      1,
			Amount:      dec1,
			CategoryID:  1,
			Description: "tails",
			Type:        "knuckles",
		})
		require.NotNil(t, err, "create request with invalid type passes successfully")
		_, err = serv.Update(context.Background(), uuid.New(), &models.UpdateTransactionRequest{
			Amount:      newOptionalCustomType[decimal.Decimal](dec1),
			CategoryID:  newOptionalConstInt64(2),
			Description: optional.NewOptionalString(&strTest),
			Type:        newOptionalCustomType[models.TransactionType](typeBad),
		})
		require.NotNil(t, err, "update request with invalid type passes successfully")
	})

	t.Run("TestInvalidFilter", func(t *testing.T) {
		_, err := serv.List(context.Background(), &models.TransactionFilter{
			UserID:     newOptionalConstInt64(-1),
			Type:       newOptionalCustomType[models.TransactionType](typeIncome),
			CategoryID: newOptionalConstInt64(1),
			Page:       newOptionalConstInt(1),
			PageSize:   newOptionalConstInt(2),
		})
		require.NotNil(t, err, "list request with filter with invalid ID passes successfully")
		_, err = serv.List(context.Background(), &models.TransactionFilter{
			UserID:     newOptionalConstInt64(1),
			Type:       newOptionalCustomType[models.TransactionType](typeBad),
			CategoryID: newOptionalConstInt64(2),
			Page:       newOptionalConstInt(1),
			PageSize:   newOptionalConstInt(1024),
		})
		require.NotNil(t, err, "list request with filter with invalid type passes successfully")
		_, err = serv.List(context.Background(), &models.TransactionFilter{
			UserID:     newOptionalConstInt64(1),
			Type:       newOptionalCustomType[models.TransactionType](typeExpense),
			CategoryID: newOptionalConstInt64(-5),
			Page:       newOptionalConstInt(1),
			PageSize:   newOptionalConstInt(1024),
		})
		require.NotNil(t, err, "list request with filter with invalid category passes successfully")
		_, err = serv.List(context.Background(), &models.TransactionFilter{
			UserID:     newOptionalConstInt64(1),
			Type:       newOptionalCustomType[models.TransactionType](typeExpense),
			CategoryID: newOptionalConstInt64(3),
			Page:       newOptionalConstInt(-5),
			PageSize:   newOptionalConstInt(2),
		})
		require.NotNil(t, err, "list request with filter with negative page passes successfully")
		_, err = serv.List(context.Background(), &models.TransactionFilter{
			UserID:     newOptionalConstInt64(1),
			Type:       newOptionalCustomType[models.TransactionType](typeExpense),
			CategoryID: newOptionalConstInt64(2),
			Page:       newOptionalConstInt(3),
			PageSize:   newOptionalConstInt(-10),
		})
		require.NotNil(t, err, "list request with filter with negative page size passes successfully")
		_, err = serv.List(context.Background(), &models.TransactionFilter{
			UserID:     newOptionalConstInt64(1),
			Type:       newOptionalCustomType[models.TransactionType](typeExpense),
			CategoryID: newOptionalConstInt64(1),
			Page:       newOptionalConstInt(3),
			PageSize:   newOptionalConstInt(0),
		})
		require.NotNil(t, err, "list request with filter with zero page size passes successfully")
	})
}

func TestGetProfit(t *testing.T) {
	var logger *slog.Logger = slog.Default()

	t.Run("TestValidProfitRequest", func(t *testing.T) {
		repo := new(storage.MockRepository)

		// Mock data: profit values for 5 days
		startDate := time.Date(2025, 1, 1, 0, 0, 0, 0, time.UTC)
		mockData := []models.ProfitDataPoint{
			{Timestamp: startDate, Profit: decimal.NewFromInt(100)},
			{Timestamp: startDate.Add(24 * time.Hour), Profit: decimal.NewFromInt(0)},
			{Timestamp: startDate.Add(48 * time.Hour), Profit: decimal.NewFromInt(50)},
			{Timestamp: startDate.Add(72 * time.Hour), Profit: decimal.NewFromInt(0)},
			{Timestamp: startDate.Add(96 * time.Hour), Profit: decimal.NewFromInt(30)},
		}

		repo.On("GetProfitByPeriods", mock.Anything, int64(1), mock.Anything, mock.Anything, int64(86400)).
			Return(mockData, nil)

		serv := service.NewTransactionService(repo, logger)

		req := &models.ProfitRequest{
			StartDate: startDate,
			EndDate:   startDate.Add(5 * 24 * time.Hour),
			Interval:  86400, // 1 day
		}

		resp, err := serv.GetProfit(context.Background(), 1, req)
		require.Nil(t, err, "valid profit request returns an error")
		require.NotNil(t, resp, "valid profit request returns nil response")
		require.Equal(t, 5, len(resp.Data), "response should have 5 data points")

		// Check cumulative profit calculation
		require.Equal(t, "100", resp.Data[0].Profit.String(), "first profit should be 100")
		require.Equal(t, "100", resp.Data[1].Profit.String(), "second profit should be 100 (cumulative)")
		require.Equal(t, "150", resp.Data[2].Profit.String(), "third profit should be 150 (100+0+50)")
		require.Equal(t, "150", resp.Data[3].Profit.String(), "fourth profit should be 150 (cumulative)")
		require.Equal(t, "180", resp.Data[4].Profit.String(), "fifth profit should be 180 (150+0+30)")
	})

	t.Run("TestInvalidDateRange", func(t *testing.T) {
		repo := new(storage.MockRepository)
		serv := service.NewTransactionService(repo, logger)

		startDate := time.Date(2025, 1, 10, 0, 0, 0, 0, time.UTC)
		endDate := time.Date(2025, 1, 1, 0, 0, 0, 0, time.UTC) // end before start

		req := &models.ProfitRequest{
			StartDate: startDate,
			EndDate:   endDate,
			Interval:  86400,
		}

		_, err := serv.GetProfit(context.Background(), 1, req)
		require.NotNil(t, err, "profit request with end_date < start_date should fail")
	})

	t.Run("TestTooManyPeriods", func(t *testing.T) {
		repo := new(storage.MockRepository)
		serv := service.NewTransactionService(repo, logger)

		startDate := time.Date(2025, 1, 1, 0, 0, 0, 0, time.UTC)
		endDate := time.Date(2025, 12, 31, 23, 59, 59, 0, time.UTC) // 1 year

		req := &models.ProfitRequest{
			StartDate: startDate,
			EndDate:   endDate,
			Interval:  60, // 1 minute intervals = too many periods
		}

		_, err := serv.GetProfit(context.Background(), 1, req)
		require.NotNil(t, err, "profit request with >1000 periods should fail")
		require.Contains(t, err.Error(), "too many periods", "error should mention too many periods")
	})

	t.Run("TestInvalidInterval", func(t *testing.T) {
		repo := new(storage.MockRepository)
		serv := service.NewTransactionService(repo, logger)

		startDate := time.Date(2025, 1, 1, 0, 0, 0, 0, time.UTC)
		endDate := time.Date(2025, 1, 10, 0, 0, 0, 0, time.UTC)

		// Test with interval = 0
		req := &models.ProfitRequest{
			StartDate: startDate,
			EndDate:   endDate,
			Interval:  0,
		}

		_, err := serv.GetProfit(context.Background(), 1, req)
		require.NotNil(t, err, "profit request with zero interval should fail")

		// Test with negative interval (caught by validation)
		req2 := &models.ProfitRequest{
			StartDate: startDate,
			EndDate:   endDate,
			Interval:  -100,
		}

		_, err = serv.GetProfit(context.Background(), 1, req2)
		require.NotNil(t, err, "profit request with negative interval should fail")
	})

	t.Run("TestIntervalTooLarge", func(t *testing.T) {
		repo := new(storage.MockRepository)
		serv := service.NewTransactionService(repo, logger)

		startDate := time.Date(2025, 1, 1, 0, 0, 0, 0, time.UTC)
		endDate := time.Date(2025, 1, 2, 0, 0, 0, 0, time.UTC) // 1 day range

		req := &models.ProfitRequest{
			StartDate: startDate,
			EndDate:   endDate,
			Interval:  86400 * 10, // 10 days interval for 1 day range
		}

		_, err := serv.GetProfit(context.Background(), 1, req)
		require.NotNil(t, err, "profit request with interval > date range should fail")
		require.Contains(t, err.Error(), "interval too large", "error should mention interval too large")
	})

	t.Run("TestEmptyDataPoints", func(t *testing.T) {
		repo := new(storage.MockRepository)

		// Mock returns empty array
		repo.On("GetProfitByPeriods", mock.Anything, int64(1), mock.Anything, mock.Anything, int64(86400)).
			Return([]models.ProfitDataPoint{}, nil)

		serv := service.NewTransactionService(repo, logger)

		startDate := time.Date(2025, 1, 1, 0, 0, 0, 0, time.UTC)
		req := &models.ProfitRequest{
			StartDate: startDate,
			EndDate:   startDate.Add(24 * time.Hour),
			Interval:  86400,
		}

		resp, err := serv.GetProfit(context.Background(), 1, req)
		require.Nil(t, err, "profit request with empty data should not fail")
		require.NotNil(t, resp, "response should not be nil")
		require.Equal(t, 0, len(resp.Data), "response should have 0 data points")
	})
}

// Довольно неудобная библиотека

func newOptionalConstInt(v int) optional.OptionalInt {
	var val int = v
	return optional.NewOptionalInt(&val)
}

func newOptionalConstInt64(v int64) optional.OptionalInt64 {
	var val int64 = v
	return optional.NewOptionalInt64(&val)
}

func newOptionalCustomType[T any](val T) optional.OptionalType[T] {
	tp := optional.NewOptionalType(&val)
	return optional.OptionalType[T]{
		Value:   val,
		Valid:   tp.Valid,
		Defined: tp.Defined,
	}
}
