package models

import (
	"fmt"
	"net/http"
)

// ErrorType represents the type of error
type ErrorType string

const (
	ErrorTypeValidation   ErrorType = "validation_error"
	ErrorTypeNotFound     ErrorType = "not_found"
	ErrorTypeUnauthorized ErrorType = "unauthorized"
	ErrorTypeConflict     ErrorType = "conflict"
	ErrorTypeInternal     ErrorType = "internal_error"
	ErrorTypeTokenExpired ErrorType = "token_expired"
	ErrorTypeInvalidToken ErrorType = "invalid_token"
)

// AuthError represents an authentication error with HTTP status code
type AuthError struct {
	Code    int                    `json:"-"`
	Type    ErrorType              `json:"type"`
	Message string                 `json:"message"`
	Details map[string]interface{} `json:"details,omitempty"`
}

// Error implements the error interface
func (e *AuthError) Error() string {
	return fmt.Sprintf("%s: %s", e.Type, e.Message)
}

// HTTPStatusCode returns the HTTP status code for the error type
func (e *AuthError) HTTPStatusCode() int {
	return e.Code
}

// ErrorResponse represents an error response (deprecated, use AuthError)
type ErrorResponse struct {
	Type    ErrorType              `json:"type"`
	Message string                 `json:"message"`
	Details map[string]interface{} `json:"details,omitempty"`
}

// Error implements the error interface
func (e *ErrorResponse) Error() string {
	return fmt.Sprintf("%s: %s", e.Type, e.Message)
}

// NewValidationError creates a new validation error
func NewValidationError(message string, details map[string]interface{}) *AuthError {
	return &AuthError{
		Code:    http.StatusBadRequest,
		Type:    ErrorTypeValidation,
		Message: message,
		Details: details,
	}
}

// NewNotFoundError creates a new not found error
func NewNotFoundError(message string) *AuthError {
	return &AuthError{
		Code:    http.StatusNotFound,
		Type:    ErrorTypeNotFound,
		Message: message,
	}
}

// NewUnauthorizedError creates a new unauthorized error
func NewUnauthorizedError(message string) *AuthError {
	return &AuthError{
		Code:    http.StatusUnauthorized,
		Type:    ErrorTypeUnauthorized,
		Message: message,
	}
}

// NewConflictError creates a new conflict error
func NewConflictError(message string) *AuthError {
	return &AuthError{
		Code:    http.StatusConflict,
		Type:    ErrorTypeConflict,
		Message: message,
	}
}

// NewInternalError creates a new internal error
func NewInternalError(message string) *AuthError {
	return &AuthError{
		Code:    http.StatusInternalServerError,
		Type:    ErrorTypeInternal,
		Message: message,
	}
}

// NewTokenExpiredError creates a new token expired error
func NewTokenExpiredError(message string) *AuthError {
	return &AuthError{
		Code:    http.StatusUnauthorized,
		Type:    ErrorTypeTokenExpired,
		Message: message,
	}
}

// NewInvalidTokenError creates a new invalid token error
func NewInvalidTokenError(message string) *AuthError {
	return &AuthError{
		Code:    http.StatusUnauthorized,
		Type:    ErrorTypeInvalidToken,
		Message: message,
	}
}
