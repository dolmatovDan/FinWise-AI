package models

import "time"

// User represents a user account
type User struct {
	ID           int64     `json:"id" db:"id"`
	Email        string    `json:"email" db:"email"`
	PasswordHash string    `json:"-" db:"password_hash"` // Never expose password hash in JSON
	FullName     string    `json:"full_name" db:"full_name"`
	CreatedAt    time.Time `json:"created_at" db:"created_at"`
	UpdatedAt    time.Time `json:"updated_at" db:"updated_at"`
}

// RegisterRequest represents request to register a new user
type RegisterRequest struct {
	Email    string `json:"email" binding:"required" validate:"required,email,max=255"`
	Password string `json:"password" binding:"required" validate:"required,min=8,max=72"` // bcrypt max length is 72
	FullName string `json:"full_name" validate:"omitempty,max=255"`
}

// LoginRequest represents request to login
type LoginRequest struct {
	Email    string `json:"email" binding:"required" validate:"required,email"`
	Password string `json:"password" binding:"required" validate:"required"`
}

// LoginResponse represents response after successful login
type LoginResponse struct {
	AccessToken  string `json:"access_token"`
	RefreshToken string `json:"refresh_token"`
	User         User   `json:"user"`
}

// UserResponse represents user data in response
type UserResponse struct {
	ID        int64     `json:"id"`
	Email     string    `json:"email"`
	FullName  string    `json:"full_name"`
	CreatedAt time.Time `json:"created_at"`
}

// ToUserResponse converts User to UserResponse (safe for public exposure)
func (u *User) ToUserResponse() UserResponse {
	return UserResponse{
		ID:        u.ID,
		Email:     u.Email,
		FullName:  u.FullName,
		CreatedAt: u.CreatedAt,
	}
}
