# Builder stage
FROM golang:1.24-alpine AS builder

# Install build dependencies
RUN apk add --no-cache git ca-certificates tzdata

# Set working directory
WORKDIR /build

# Copy go mod files
COPY go.mod go.sum ./

# Download dependencies
RUN go mod download

# Copy source code
COPY ml-api/ ./ml-api/

# Build the application
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build \
    -ldflags="-w -s" \
    -o /build/app \
    ./ml-api/cmd/app/main.go

# Final stage
# TODO: change to pytorch image once models ready
# FROM pytorch/pytorch:latest
FROM python:3.12-slim

# Install CA certificates for HTTPS
# DEBUG
# RUN apk --no-cache add ca-certificates tzdata
RUN apt-get update
RUN apt-get install ca-certificates
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y tzdata

# Create non-root user
RUN addgroup --system --gid 1001 --system appuser && \
    adduser --system --uid 1001 appuser --gid 1001

# Set working directory
WORKDIR /app

# Copy binary from builder
COPY --from=builder /build/app .
RUN chmod +x app

# Set up Python
# TODO: uncomment once models ready
# COPY --from=builder /build/ml-api/python ./python
# RUN pip install --no-cache-dir --upgrade pip
# RUN pip install --no-cache-dir -r python/requirements.txt

# Change ownership
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8081

# Run the application
CMD ["./app"]
