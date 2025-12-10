package middleware

import (
	"strconv"
	"time"

	"github.com/gin-gonic/gin"
)

// PrometheusMiddleware returns a Gin middleware that collects HTTP metrics
func PrometheusMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		HTTPInFlightRequests.Inc()
		defer HTTPInFlightRequests.Dec()

		start := time.Now()
		c.Next()
		duration := time.Since(start).Seconds()

		method := c.Request.Method
		endpoint := c.FullPath()
		if endpoint == "" {
			endpoint = "not_found"
		}
		status := strconv.Itoa(c.Writer.Status())

		HTTPRequestsTotal.WithLabelValues(method, endpoint, status).Inc()
		HTTPRequestDuration.WithLabelValues(method, endpoint).Observe(duration)
	}
}
