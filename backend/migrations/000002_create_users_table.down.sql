-- Drop trigger
DROP TRIGGER IF EXISTS update_users_updated_at ON users;

-- Remove foreign key constraint from transactions table (if exists)
-- ALTER TABLE transactions
--     DROP CONSTRAINT IF EXISTS fk_transactions_user_id;

-- Drop index
DROP INDEX IF EXISTS idx_users_email;

-- Drop table
DROP TABLE IF EXISTS users;
