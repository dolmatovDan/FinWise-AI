package mlLauncher

import (
	"os"

	"github.com/dolmatovDan/FinWise-AI/backend/ml-api/internal/models"
)

type MlLauncher interface {
	RunForecastModel(req *models.ForecastRequest) (*models.ForecastResponse, error)
	RunAdviceModel(req *models.AdviceRequest) (*models.AdviceResponse, error)
	RunReceiptScan(path models.ReceiptFilePath) (*models.ReceiptScanResponse, error)
}

func DefaultMlLauncher() MlLauncher {
	baseURL := os.Getenv("ML_BASE_URL")
	return NewHTTPMlLauncher(baseURL)
}
