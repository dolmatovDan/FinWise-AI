package service_test

import (
	"context"
	"strings"
	"time"
	"log/slog"
	"testing"

	"github.com/dolmatovDan/FinWise-AI/backend/internal/models"
	"github.com/dolmatovDan/FinWise-AI/backend/internal/service"
	"github.com/dolmatovDan/FinWise-AI/backend/internal/storage"
    "github.com/stretchr/testify/require"
    "github.com/stretchr/testify/mock"
	"github.com/google/uuid"
	"github.com/shopspring/decimal"
)

func TestServiceValidator(t *testing.T) {
	dec1, _ := decimal.NewFromString("12.34")
	dec2, _ := decimal.NewFromString("56.78")
	decNegative, _ := decimal.NewFromString("-12.34")
	decTooLarge, _ := decimal.NewFromString("123456789123456789123456.512345678")

	fakeTransaction := models.Transaction{
		ID: uuid.New(),
		UserID: 1,
		Amount: dec1,
		Category: "sonic",
		Description: "tails",
		Type: models.TransactionTypeIncome,
		CreatedAt: time.Now(),
		UpdatedAt: time.Now(),
	}

	var repo *storage.MockRepository = new(storage.MockRepository)
	repo.On("Create", mock.Anything, mock.Anything).Return(&fakeTransaction, nil)
	repo.On("Update", mock.Anything, mock.Anything, mock.Anything).Return(&fakeTransaction, nil)
	repo.On("List", mock.Anything, mock.Anything).Return(&models.TransactionListResponse{
		Transactions: []models.Transaction{fakeTransaction},
		Total: 1,
		Page: 1,
		PageSize: 1,
	}, nil)
	
	var logger *slog.Logger = slog.Default()
	serv := service.NewTransactionService(repo, logger)

	// TODO: get rid of string pointers in service implementation
	strTest := "sonic"
	strLong := strings.Repeat("sonic", 100)
	typeExpense := models.TransactionTypeExpense
	typeIncome := models.TransactionTypeIncome
	var typeBad models.TransactionType = "knuckles"

	t.Run("TestValidRequest", func(t *testing.T) {
		_, err := serv.Create(context.Background(), &models.CreateTransactionRequest{
			UserID: 1,
			Amount: dec1,
			Category: "sonic",
			Description: "tails",
			Type: "income",
		})
		require.Nil(t, err, "valid create request #1 returns an error")
		_, err = serv.Create(context.Background(), &models.CreateTransactionRequest{
			UserID: 2,
			Amount: dec2,
			Category: "knuckles",
			Description: "",
			Type: "expense",
		})
		require.Nil(t, err, "valid create request #2 returns an error")
		_, err = serv.Update(context.Background(), uuid.New(), &models.UpdateTransactionRequest{
			Amount: &dec1,
			Category: &strTest,
			Description: &strTest,
			Type: &typeIncome,
		})
		require.Nil(t, err, "valid update request #1 returns an error")
		_, err = serv.Update(context.Background(), uuid.New(), &models.UpdateTransactionRequest{
			Amount: &dec2,
			Category: &strTest,
			Description: &strTest,
			Type: &typeExpense,
		})
		require.Nil(t, err, "valid update request #2 returns an error")
	})

	t.Run("TestInvalidUserId", func(t *testing.T) {
		_, err := serv.Create(context.Background(), &models.CreateTransactionRequest{
			UserID: -4,
			Amount: dec1,
			Category: "sonic",
			Description: "tails",
			Type: "income",
		})
		require.NotNil(t, err, "request with negative user ID passes successfully")
		_, err = serv.Create(context.Background(), &models.CreateTransactionRequest{
			UserID: 0,
			Amount: dec1,
			Category: "sonic",
			Description: "tails",
			Type: "expense",
		})
		require.NotNil(t, err, "request with zero user ID passes successfully")
	})

	t.Run("TestInvalidAmount", func(t *testing.T) {
		_, err := serv.Create(context.Background(), &models.CreateTransactionRequest{
			UserID: 1,
			Amount: decNegative,
			Category: "sonic",
			Description: "tails",
			Type: "income",
		})
		require.NotNil(t, err, "create request with negative amount passes successfully")
		_, err = serv.Create(context.Background(), &models.CreateTransactionRequest{
			UserID: 2,
			Amount: decTooLarge,
			Category: "sonic",
			Description: "tails",
			Type: "expense",
		})
		require.NotNil(t, err, "create request with too large amount passes successfully")
		_, err = serv.Update(context.Background(), uuid.New(), &models.UpdateTransactionRequest{
			Amount: &decNegative,
			Category: &strTest,
			Description: &strTest,
			Type: &typeIncome,
		})
		require.NotNil(t, err, "update request with negative amount passes successfully")
		_, err = serv.Update(context.Background(), uuid.New(), &models.UpdateTransactionRequest{
			Amount: &decTooLarge,
			Category: &strTest,
			Description: &strTest,
			Type: &typeExpense,
		})
		require.NotNil(t, err, "update request with too large amount passes successfully")
	})

	t.Run("TestInvalidCategory", func(t *testing.T) {
		_, err := serv.Create(context.Background(), &models.CreateTransactionRequest{
			UserID: 1,
			Amount: dec1,
			Category: strLong,
			Description: "tails",
			Type: "income",
		})
		require.NotNil(t, err, "create request with too long category passes successfully")
		_, err = serv.Update(context.Background(), uuid.New(), &models.UpdateTransactionRequest{
			Amount: &dec1,
			Category: &strLong,
			Description: &strTest,
			Type: &typeIncome,
		})
		require.NotNil(t, err, "update request with too long category passes successfully")
	})

	t.Run("TestInvalidType", func(t *testing.T) {
		_, err := serv.Create(context.Background(), &models.CreateTransactionRequest{
			UserID: 1,
			Amount: dec1,
			Category: "sonic",
			Description: "tails",
			Type: "knuckles",
		})
		require.NotNil(t, err, "create request with invalid type passes successfully")
		_, err = serv.Update(context.Background(), uuid.New(), &models.UpdateTransactionRequest{
			Amount: &dec1,
			Category: &strTest,
			Description: &strTest,
			Type: &typeBad,
		})
		require.NotNil(t, err, "update request with invalid type passes successfully")
	})

	t.Run("TestInvalidFilter", func(t *testing.T) {
		_, err := serv.List(context.Background(), &models.TransactionFilter{
			UserID: -1,
			Type: &typeIncome,
			Category: &strTest,
			Page: 1,
			PageSize: 2,
		})
		require.NotNil(t, err, "list request with filter with invalid ID passes successfully")
		_, err = serv.List(context.Background(), &models.TransactionFilter{
			UserID: 1,
			Type: &typeBad,
			Category: &strTest,
			Page: 1,
			PageSize: 1024,
		})
		require.NotNil(t, err, "list request with filter with invalid type passes successfully")
		_, err = serv.List(context.Background(), &models.TransactionFilter{
			UserID: 1,
			Type: &typeExpense,
			Category: &strLong,
			Page: 1,
			PageSize: 1024,
		})
		require.NotNil(t, err, "list request with filter with invalid category passes successfully")
		_, err = serv.List(context.Background(), &models.TransactionFilter{
			UserID: 1,
			Type: &typeExpense,
			Category: &strTest,
			Page: -5,
			PageSize: 2,
		})
		require.NotNil(t, err, "list request with filter with negative page passes successfully")
		_, err = serv.List(context.Background(), &models.TransactionFilter{
			UserID: 1,
			Type: &typeExpense,
			Category: &strTest,
			Page: 3,
			PageSize: -10,
		})
		require.NotNil(t, err, "list request with filter with negative page size passes successfully")
		_, err = serv.List(context.Background(), &models.TransactionFilter{
			UserID: 1,
			Type: &typeExpense,
			Category: &strTest,
			Page: 3,
			PageSize: 0,
		})
		require.NotNil(t, err, "list request with filter with zero page size passes successfully")
	})
}