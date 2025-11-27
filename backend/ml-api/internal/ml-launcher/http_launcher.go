package mlLauncher

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"mime/multipart"
	"net/http"
	"os"
	"path/filepath"
	"time"

	"github.com/dolmatovDan/FinWise-AI/backend/ml-api/internal/models"
)

type HTTPMlLauncher struct {
	client  *http.Client
	baseURL string
}

func NewHTTPMlLauncher(baseURL string) *HTTPMlLauncher {
	if baseURL == "" {
		baseURL = os.Getenv("ML_BASE_URL")
	}
	if baseURL == "" {
		baseURL = "http://localhost:8000"
	}

	timeout := 1000 * time.Second
	if ts := os.Getenv("ML_HTTP_TIMEOUT"); ts != "" {
		if d, err := time.ParseDuration(ts); err == nil {
			timeout = d
		}
	}

	return &HTTPMlLauncher{
		baseURL: baseURL,
		client: &http.Client{
			Timeout: timeout,
		},
	}
}

func (l *HTTPMlLauncher) RunForecastModel(req *models.ForecastRequest) (*models.ForecastResponse, error) {
	body, err := json.Marshal(req)
	if err != nil {
		return nil, err
	}

	url := fmt.Sprintf("%s/forecast", l.baseURL)
	httpReq, err := http.NewRequest(http.MethodPost, url, bytes.NewReader(body))
	if err != nil {
		return nil, err
	}
	httpReq.Header.Set("Content-Type", "application/json")

	resp, err := l.client.Do(httpReq)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("forecast status %d", resp.StatusCode)
	}

	var out models.ForecastResponse
	if err := json.NewDecoder(resp.Body).Decode(&out); err != nil {
		return nil, err
	}
	return &out, nil
}

func (l *HTTPMlLauncher) RunAdviceModel(req *models.AdviceRequest) (*models.AdviceResponse, error) {
	body, err := json.Marshal(req)
	if err != nil {
		return nil, err
	}

	url := fmt.Sprintf("%s/advice", l.baseURL)
	httpReq, err := http.NewRequest(http.MethodPost, url, bytes.NewReader(body))
	if err != nil {
		return nil, err
	}
	httpReq.Header.Set("Content-Type", "application/json")

	resp, err := l.client.Do(httpReq)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("advice status %d", resp.StatusCode)
	}

	var out models.AdviceResponse
	if err := json.NewDecoder(resp.Body).Decode(&out); err != nil {
		return nil, err
	}
	return &out, nil
}

func (l *HTTPMlLauncher) RunReceiptScan(path models.ReceiptFilePath) (*models.ReceiptScanResponse, error) {
	f, err := os.Open(string(path))
	if err != nil {
		return nil, fmt.Errorf("open receipt file: %w", err)
	}
	defer f.Close()

	var buf bytes.Buffer
	writer := multipart.NewWriter(&buf)

	part, err := writer.CreateFormFile("file", filepath.Base(string(path)))
	if err != nil {
		return nil, fmt.Errorf("create form file: %w", err)
	}

	if _, err := io.Copy(part, f); err != nil {
		return nil, fmt.Errorf("copy file: %w", err)
	}

	if err := writer.Close(); err != nil {
		return nil, fmt.Errorf("close multipart writer: %w", err)
	}

	url := fmt.Sprintf("%s/receipt-total-file", l.baseURL)
	req, err := http.NewRequest(http.MethodPost, url, &buf)
	if err != nil {
		return nil, err
	}
	req.Header.Set("Content-Type", writer.FormDataContentType())

	resp, err := l.client.Do(req)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("receipt status %d", resp.StatusCode)
	}

	var out models.ReceiptScanResponse
	if err := json.NewDecoder(resp.Body).Decode(&out); err != nil {
		return nil, err
	}
	return &out, nil
}

var _ MlLauncher = (*HTTPMlLauncher)(nil)
