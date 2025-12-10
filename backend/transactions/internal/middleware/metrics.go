package middleware

import (
	"github.com/prometheus/client_golang/prometheus"
	"github.com/prometheus/client_golang/prometheus/promauto"
)

var (
	// HTTPRequestsTotal counts total number of HTTP requests
	HTTPRequestsTotal = promauto.NewCounterVec(
		prometheus.CounterOpts{
			Namespace: "transactions_service",
			Name:      "http_requests_total",
			Help:      "Total number of HTTP requests",
		},
		[]string{"method", "endpoint", "status"},
	)

	// HTTPRequestDuration measures HTTP request processing time
	HTTPRequestDuration = promauto.NewHistogramVec(
		prometheus.HistogramOpts{
			Namespace: "transactions_service",
			Name:      "http_request_duration_seconds",
			Help:      "HTTP request duration in seconds",
			Buckets:   prometheus.DefBuckets,
		},
		[]string{"method", "endpoint"},
	)

	// HTTPInFlightRequests tracks current number of requests being processed
	HTTPInFlightRequests = promauto.NewGauge(
		prometheus.GaugeOpts{
			Namespace: "transactions_service",
			Name:      "http_in_flight_requests",
			Help:      "Current number of HTTP requests being processed",
		},
	)
)
