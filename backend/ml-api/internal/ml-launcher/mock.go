package mlLauncher

import (
	"time"

	"github.com/dolmatovDan/FinWise-AI/backend/ml-api/internal/models"
	"github.com/shopspring/decimal"
	"github.com/stretchr/testify/mock"
)

type MockMlLauncher struct {
	mock.Mock
}

func (l *MockMlLauncher) RunForecastModel(req *models.ForecastRequest) (*models.ForecastResponse, error) {
	args := l.Called(req)
	err := args.Error(1)
	if tr, ok := args.Get(0).(*models.ForecastResponse); ok {
		return tr, err
	} else {
		return nil, err
	}
}

func (l *MockMlLauncher) RunAdviceModel(req *models.AdviceRequest) (*models.AdviceResponse, error) {
	args := l.Called(req)
	err := args.Error(1)
	if tr, ok := args.Get(0).(*models.AdviceResponse); ok {
		return tr, err
	} else {
		return nil, err
	}
}

// compile check
var _ MlLauncher = (*MockMlLauncher)(nil)

type StubMlLauncher struct{}

func (l *StubMlLauncher) RunForecastModel(req *models.ForecastRequest) (*models.ForecastResponse, error) {
	var ans models.ForecastResponse
	var duration time.Duration
	if req.Granularity == "month" {
		duration = time.Duration(30*24) * time.Hour
	} else {
		duration = time.Duration(365*24) * time.Hour
	}

	periodEnd := time.Now()
	for i := int64(0); i < req.Steps; i++ {
		periodEnd = periodEnd.Add(duration)
		ans.PeriodEnd = append(ans.PeriodEnd, periodEnd)
		ans.ExpenseForecast = append(ans.ExpenseForecast, decimal.NewFromInt(100*i))
		ans.IncomeForecast = append(ans.IncomeForecast, decimal.NewFromInt(50*i))
	}

	return &ans, nil
}

func (l *StubMlLauncher) RunAdviceModel(*models.AdviceRequest) (*models.AdviceResponse, error) {
	return &models.AdviceResponse{
		Advice: "Подъём в 5 утра, контрастный душ, диета, фитнес, интервальное голодание, книги по саморазвитию, психологические тренинги, позитивное мышление и желательно влиятельный родственник",
	}, nil
}

var _ MlLauncher = (*StubMlLauncher)(nil)
