# Auth Service Database Migrations

This directory is reserved for auth-service specific migrations.

## Current Setup

The auth service shares the same PostgreSQL database with the transactions service. All user-related tables are managed through the **transactions/migrations** directory:

- `000002_create_users_table.up.sql` - Users table
- `000003_add_transactions_user_fk.up.sql` - Foreign key constraint
- `000004_create_refresh_tokens_table.up.sql` - Refresh tokens table

## Future Migrations

If the auth service needs to be separated into its own database, migrations can be added here.

## Running Migrations

Migrations are currently run from the transactions service:

```bash
# From transactions directory
go run cmd/app/main.go migrate
```

Or using a migration tool like `golang-migrate`:

```bash
migrate -path transactions/migrations -database "postgresql://user:pass@localhost:5432/finwise?sslmode=disable" up
```
