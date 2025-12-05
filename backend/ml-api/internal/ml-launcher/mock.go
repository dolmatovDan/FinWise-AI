package mlLauncher

import (
	"github.com/dolmatovDan/FinWise-AI/backend/ml-api/internal/models"
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
	}
	return nil, err
}

func (l *MockMlLauncher) RunAdviceModel(req *models.AdviceRequest) (*models.AdviceResponse, error) {
	args := l.Called(req)
	err := args.Error(1)
	if tr, ok := args.Get(0).(*models.AdviceResponse); ok {
		return tr, err
	}
	return nil, err
}

func (l *MockMlLauncher) RunReceiptScan(path models.ReceiptFilePath) (*models.ReceiptScanResponse, error) {
	args := l.Called(path)
	err := args.Error(1)
	if tr, ok := args.Get(0).(*models.ReceiptScanResponse); ok {
		return tr, err
	}
	return nil, err
}

var _ MlLauncher = (*MockMlLauncher)(nil)
