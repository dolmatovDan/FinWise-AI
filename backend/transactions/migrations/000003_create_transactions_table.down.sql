-- Drop trigger
DROP TRIGGER IF EXISTS update_transaction_updated_at ON transaction;

-- Drop function
DROP FUNCTION IF EXISTS update_updated_at_column();

-- Drop indexes
DROP INDEX IF EXISTS idx_transaction_created_at;
DROP INDEX IF EXISTS idx_transaction_category_id;
DROP INDEX IF EXISTS idx_transaction_user_id;

-- Drop table
DROP TABLE IF EXISTS transaction;
