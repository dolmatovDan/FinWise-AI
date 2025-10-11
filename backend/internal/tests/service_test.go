package tests

import (
	"context"
	"strings"
	"log/slog"
	"testing"

	"github.com/dolmatovDan/FinWise-AI/backend/internal/models"
	"github.com/dolmatovDan/FinWise-AI/backend/internal/service"
	"github.com/dolmatovDan/FinWise-AI/backend/internal/storage"
    "github.com/stretchr/testify/require"
	"github.com/google/uuid"
	"github.com/shopspring/decimal"
)

var logger = slog.Default()
var repo storage.TransactionRepository = &StubRepository{}

func TestValidRequest(t *testing.T) {
	serv := service.NewTransactionService(repo, logger)
	fail_msg := "Tests: valid request returns an error"
	dec1, _ := decimal.NewFromString("12.34")
	dec2, _ := decimal.NewFromString("56.78")
	str_test := "sonic"
	type_expense := models.TransactionTypeExpense
	type_income := models.TransactionTypeIncome

	_, err := serv.Create(context.Background(), &models.CreateTransactionRequest{
		UserID: 1,
		Amount: dec1,
		Category: "sonic",
		Description: "tails",
		Type: "income",
	})
	require.Nil(t, err, fail_msg)
	_, err = serv.Create(context.Background(), &models.CreateTransactionRequest{
		UserID: 2,
		Amount: dec2,
		Category: "knuckles",
		Description: "",
		Type: "expense",
	})
	require.Nil(t, err, fail_msg)
	_, err = serv.Update(context.Background(), uuid.New(), &models.UpdateTransactionRequest{
		Amount: &dec1,
		Category: &str_test,
		Description: &str_test,
		Type: &type_income,
	})
	require.Nil(t, err, fail_msg)
	_, err = serv.Update(context.Background(), uuid.New(), &models.UpdateTransactionRequest{
		Amount: &dec2,
		Category: &str_test,
		Description: &str_test,
		Type: &type_expense,
	})
	require.Nil(t, err, fail_msg)
}

func TestInvalidUserId(t *testing.T) {
	serv := service.NewTransactionService(repo, logger)
	fail_msg := "Tests: request with invalid user ID passes successfully"
	dec1, _ := decimal.NewFromString("12.34")

	_, err := serv.Create(context.Background(), &models.CreateTransactionRequest{
		UserID: -4,
		Amount: dec1,
		Category: "sonic",
		Description: "tails",
		Type: "income",
	})
	require.NotNil(t, err, fail_msg)
	_, err = serv.Create(context.Background(), &models.CreateTransactionRequest{
		UserID: 0,
		Amount: dec1,
		Category: "sonic",
		Description: "tails",
		Type: "expense",
	})
	require.NotNil(t, err, fail_msg)
}

func TestInvalidAmount(t *testing.T) {
	serv := service.NewTransactionService(repo, logger)
	fail_msg := "Tests: request with invalid amount passes successfully"
	dec1_bad, _ := decimal.NewFromString("-12.34")
	dec2_bad, _ := decimal.NewFromString("123456789123456789123456.512345678")

	_, err := serv.Create(context.Background(), &models.CreateTransactionRequest{
		UserID: 1,
		Amount: dec1_bad,
		Category: "sonic",
		Description: "tails",
		Type: "income",
	})
	require.NotNil(t, err, fail_msg)
	_, err = serv.Create(context.Background(), &models.CreateTransactionRequest{
		UserID: 2,
		Amount: dec2_bad,
		Category: "sonic",
		Description: "tails",
		Type: "expense",
	})
	require.NotNil(t, err, fail_msg)
}

func TestInvalidCategory(t *testing.T) {
	serv := service.NewTransactionService(repo, logger)
	fail_msg := "Tests: request with invalid category passes successfully"
	dec1, _ := decimal.NewFromString("12.34")

	_, err := serv.Create(context.Background(), &models.CreateTransactionRequest{
		UserID: 1,
		Amount: dec1,
		Category: strings.Repeat("sonic", 100),
		Description: "tails",
		Type: "income",
	})
	require.NotNil(t, err, fail_msg)
}

func TestInvalidType(t *testing.T) {
	serv := service.NewTransactionService(repo, logger)
	fail_msg := "Tests: request with invalid type passes successfully"
	dec1, _ := decimal.NewFromString("12.34")

	_, err := serv.Create(context.Background(), &models.CreateTransactionRequest{
		UserID: 1,
		Amount: dec1,
		Category: "sonic",
		Description: "tails",
		Type: "knuckles",
	})
	require.NotNil(t, err, fail_msg)
}
