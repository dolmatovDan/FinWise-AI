package postgres

import (
	"context"
	"errors"
	"fmt"

	"github.com/dolmatovDan/FinWise-AI/backend/auth/internal/models"
	"github.com/dolmatovDan/FinWise-AI/backend/auth/internal/storage"
	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgconn"
)

// UserStorage handles user database operations
type UserStorage struct {
	storage *Storage
}

// Compile-time check to ensure UserStorage implements storage.UserRepository
var _ storage.UserRepository = (*UserStorage)(nil)

// NewUserStorage creates a new user storage instance
func NewUserStorage(storage *Storage) *UserStorage {
	return &UserStorage{
		storage: storage,
	}
}

// Create creates a new user
func (us *UserStorage) Create(ctx context.Context, user *models.User) error {
	us.storage.logger.Info("creating new user", "email", user.Email)

	query := `
		INSERT INTO "user" (email, password_hash, full_name)
		VALUES ($1, $2, $3)
		RETURNING id, created_at, updated_at
	`

	err := us.storage.pool.QueryRow(ctx, query,
		user.Email,
		user.PasswordHash,
		user.FullName,
	).Scan(
		&user.ID,
		&user.CreatedAt,
		&user.UpdatedAt,
	)

	if err != nil {
		// Check for unique constraint violation (duplicate email)
		var pgErr *pgconn.PgError
		if errors.As(err, &pgErr) && pgErr.Code == "23505" { // unique_violation
			us.storage.logger.Warn("user with this email already exists", "email", user.Email)
			return fmt.Errorf("user with email %s already exists", user.Email)
		}

		us.storage.logger.Error("failed to create user", "error", err)
		return fmt.Errorf("failed to create user: %w", err)
	}

	us.storage.logger.Info("user created successfully", "id", user.ID, "email", user.Email)
	return nil
}

// GetByEmail retrieves a user by email
func (us *UserStorage) GetByEmail(ctx context.Context, email string) (*models.User, error) {
	us.storage.logger.Info("fetching user by email", "email", email)

	query := `
		SELECT id, email, password_hash, full_name, created_at, updated_at
		FROM "user"
		WHERE email = $1
	`

	var user models.User
	err := us.storage.pool.QueryRow(ctx, query, email).Scan(
		&user.ID,
		&user.Email,
		&user.PasswordHash,
		&user.FullName,
		&user.CreatedAt,
		&user.UpdatedAt,
	)

	if err != nil {
		if errors.Is(err, pgx.ErrNoRows) {
			us.storage.logger.Info("user not found", "email", email)
			return nil, fmt.Errorf("user with email %s not found", email)
		}

		us.storage.logger.Error("failed to fetch user by email", "error", err)
		return nil, fmt.Errorf("failed to fetch user: %w", err)
	}

	us.storage.logger.Info("user fetched successfully", "id", user.ID, "email", email)
	return &user, nil
}

// GetByID retrieves a user by ID
func (us *UserStorage) GetByID(ctx context.Context, id int64) (*models.User, error) {
	us.storage.logger.Info("fetching user by id", "id", id)

	query := `
		SELECT id, email, password_hash, full_name, created_at, updated_at
		FROM "user"
		WHERE id = $1
	`

	var user models.User
	err := us.storage.pool.QueryRow(ctx, query, id).Scan(
		&user.ID,
		&user.Email,
		&user.PasswordHash,
		&user.FullName,
		&user.CreatedAt,
		&user.UpdatedAt,
	)

	if err != nil {
		if errors.Is(err, pgx.ErrNoRows) {
			us.storage.logger.Info("user not found", "id", id)
			return nil, fmt.Errorf("user with id %d not found", id)
		}

		us.storage.logger.Error("failed to fetch user by id", "error", err)
		return nil, fmt.Errorf("failed to fetch user: %w", err)
	}

	us.storage.logger.Info("user fetched successfully", "id", user.ID)
	return &user, nil
}

// EmailExists checks if an email already exists
func (us *UserStorage) EmailExists(ctx context.Context, email string) (bool, error) {
	us.storage.logger.Info("checking if email exists", "email", email)

	query := `
		SELECT EXISTS(SELECT 1 FROM "user" WHERE email = $1)
	`

	var exists bool
	err := us.storage.pool.QueryRow(ctx, query, email).Scan(&exists)
	if err != nil {
		us.storage.logger.Error("failed to check email existence", "error", err)
		return false, fmt.Errorf("failed to check email existence: %w", err)
	}

	us.storage.logger.Info("email existence check complete", "email", email, "exists", exists)
	return exists, nil
}
