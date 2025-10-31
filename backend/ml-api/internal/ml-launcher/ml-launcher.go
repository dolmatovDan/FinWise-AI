package mlLauncher

import (
	"encoding/json"
	"io"
	"os/exec"

	"github.com/dolmatovDan/FinWise-AI/backend/ml-api/internal/models"
)

type MlLauncher interface {
	RunForecastModel(req *models.ForecastRequest) (*models.ForecastResponse, error)
	RunAdviceModel(req *models.AdviceRequest) (*models.AdviceResponse, error)
}

type PythonMlLauncher struct {
	Dir                string
	ForecastScriptName string
	AdviceScriptName   string
}

func DefaultMlLauncher() MlLauncher {
	return &PythonMlLauncher{
		Dir:                "ml",
		ForecastScriptName: "forecast.py",
		AdviceScriptName:   "advice.py",
	}
}

func (l *PythonMlLauncher) RunForecastModel(req *models.ForecastRequest) (*models.ForecastResponse, error) {
	cmd := exec.Command("python3", l.Dir+"/"+l.ForecastScriptName)
	stdin, err := cmd.StdinPipe()
	if err != nil {
		return nil, err
	}

	reqJson, err := json.Marshal(req)
	if err != nil {
		return nil, err
	}
	io.WriteString(stdin, string(reqJson))

	outputJson, err := cmd.Output()
	if err != nil {
		return nil, err
	}

	var out models.ForecastResponse
	if err := json.Unmarshal(outputJson, &out); err != nil {
		return nil, err
	}

	return &out, nil
}

func (l *PythonMlLauncher) RunAdviceModel(req *models.AdviceRequest) (*models.AdviceResponse, error) {
	cmd := exec.Command("python3", l.Dir+"/"+l.AdviceScriptName)
	stdin, err := cmd.StdinPipe()
	if err != nil {
		return nil, err
	}

	reqJson, err := json.Marshal(req)
	if err != nil {
		return nil, err
	}
	io.WriteString(stdin, string(reqJson))

	outputJson, err := cmd.Output()
	if err != nil {
		return nil, err
	}

	var out models.AdviceResponse
	if err := json.Unmarshal(outputJson, &out); err != nil {
		return nil, err
	}

	return &out, nil
}
