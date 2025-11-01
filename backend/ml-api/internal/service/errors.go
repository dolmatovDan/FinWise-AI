package service

import "errors"

var (
	// ErrValidation is returned when validation fails
	ErrValidation = errors.New("validation error")

	// ErrValidation is returned when validation fails
	ErrMl = errors.New("ML model error")

	// ErrNotFound is returned when entity is not found
	ErrNotFound = errors.New("not found")
)
