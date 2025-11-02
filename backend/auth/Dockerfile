# Builder stage
FROM golang:1.24-alpine AS builder

RUN apk add --no-cache git ca-certificates tzdata

WORKDIR /build

COPY go.mod go.sum ./

RUN go mod download

COPY auth/ ./auth/

RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build \
    -ldflags="-w -s" \
    -o /build/app \
    ./auth/cmd/app/main.go

# Final stage
FROM alpine:latest

RUN apk --no-cache add ca-certificates tzdata

RUN addgroup -g 1001 -S appuser && \
    adduser -u 1001 -S appuser -G appuser

WORKDIR /app

COPY --from=builder /build/app .
RUN chmod +x app

COPY --from=builder /build/auth/migrations ./migrations

COPY --from=builder /build/auth/keys ./keys

RUN chown -R appuser:appuser /app

USER appuser

EXPOSE 8082

CMD ["./app"]
