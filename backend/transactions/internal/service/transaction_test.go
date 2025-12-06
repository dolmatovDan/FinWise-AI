package service_test

import (
	"context"
	"log/slog"
	"testing"
	"time"

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
	// decNegative, _ := decimal.NewFromString("-12.34")
	// decTooLarge, _ := decimal.NewFromString("123456789123456789123456.512345678")

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
		models.Category{ID: 1, Name: "Packs", Description: "qwertyuiop"},
		models.Category{ID: 2, Name: "Snacks", Description: "asdfghjkl"},
		models.Category{ID: 1, Name: "Tracks", Description: "zxcvbnm"},
	})

	var int1 int64 = 1
	var int2 int64 = 2

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
			Amount:      &dec1,
			CategoryID:  &int1,
			Description: &strTest,
			Type:        &typeIncome,
		})
		require.Nil(t, err, "valid update request #1 returns an error")
		_, err = serv.Update(context.Background(), uuid.New(), &models.UpdateTransactionRequest{
			Amount:      &dec2,
			CategoryID:  &int2,
			Description: &strTest,
			Type:        &typeExpense,
		})
		require.Nil(t, err, "valid update request #2 returns an error")
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
			Amount:      &dec1,
			CategoryID:  &int2,
			Description: &strTest,
			Type:        &typeBad,
		})
		require.NotNil(t, err, "update request with invalid type passes successfully")
	})

	t.Run("TestInvalidFilter", func(t *testing.T) {
		_, err := serv.List(context.Background(), &models.TransactionFilter{
			UserID:     -1,
			Type:       &typeIncome,
			CategoryID: &int1,
			Page:       1,
			PageSize:   2,
		})
		require.NotNil(t, err, "list request with filter with invalid ID passes successfully")
		_, err = serv.List(context.Background(), &models.TransactionFilter{
			UserID:     1,
			Type:       &typeBad,
			CategoryID: &int2,
			Page:       1,
			PageSize:   1024,
		})
		require.NotNil(t, err, "list request with filter with invalid type passes successfully")
		_, err = serv.List(context.Background(), &models.TransactionFilter{
			UserID:     1,
			Type:       &typeExpense,
			CategoryID: &int2,
			Page:       -5,
			PageSize:   2,
		})
		require.NotNil(t, err, "list request with filter with negative page passes successfully")
		_, err = serv.List(context.Background(), &models.TransactionFilter{
			UserID:     1,
			Type:       &typeExpense,
			CategoryID: &int1,
			Page:       3,
			PageSize:   -10,
		})
		require.NotNil(t, err, "list request with filter with negative page size passes successfully")
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
