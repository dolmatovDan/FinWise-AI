package config

import (
	"fmt"
	"log/slog"
	"os"
	"time"
)

// Config represents application configuration
type Config struct {
	Server ServerConfig
	Logger LoggerConfig
}

// ServerConfig represents server configuration
type ServerConfig struct {
	Host            string
	Port            string
	ReadTimeout     time.Duration
	WriteTimeout    time.Duration
	ShutdownTimeout time.Duration
}

// LoggerConfig represents logger configuration
type LoggerConfig struct {
	Level  string // debug, info, warn, error
	Format string // json or text
}

func Load() (*Config, error) {
	cfg := &Config{
		Server: ServerConfig{
			Host:            getEnv("SERVER_HOST", "0.0.0.0"),
			Port:            getEnv("SERVER_PORT_ML", "8081"),
			ReadTimeout:     getDurationEnv("SERVER_READ_TIMEOUT", 1000*time.Second),
			WriteTimeout:    getDurationEnv("SERVER_WRITE_TIMEOUT", 1000*time.Second),
			ShutdownTimeout: getDurationEnv("SERVER_SHUTDOWN_TIMEOUT", 500*time.Second),
		},
		Logger: LoggerConfig{
			Level:  getEnv("LOG_LEVEL", "info"),
			Format: getEnv("LOG_FORMAT", "json"),
		},
	}

	return cfg, nil
}

// Address returns server address
func (c *ServerConfig) Address() string {
	return fmt.Sprintf("%s:%s", c.Host, c.Port)
}

// NewLogger creates a new slog logger based on configuration
func (c *LoggerConfig) NewLogger() *slog.Logger {
	var level slog.Level
	switch c.Level {
	case "debug":
		level = slog.LevelDebug
	case "info":
		level = slog.LevelInfo
	case "warn":
		level = slog.LevelWarn
	case "error":
		level = slog.LevelError
	default:
		level = slog.LevelInfo
	}

	opts := &slog.HandlerOptions{
		Level: level,
	}

	var handler slog.Handler
	if c.Format == "json" {
		handler = slog.NewJSONHandler(os.Stdout, opts)
	} else {
		handler = slog.NewTextHandler(os.Stdout, opts)
	}

	return slog.New(handler)
}

// getEnv gets environment variable or returns default value
func getEnv(key, defaultValue string) string {
	if value := os.Getenv(key); value != "" {
		return value
	}
	return defaultValue
}

// getDurationEnv gets duration environment variable or returns default value
func getDurationEnv(key string, defaultValue time.Duration) time.Duration {
	if value := os.Getenv(key); value != "" {
		if duration, err := time.ParseDuration(value); err == nil {
			return duration
		}
	}
	return defaultValue
}
