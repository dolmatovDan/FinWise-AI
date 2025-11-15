-- Drop trigger
DROP TRIGGER IF EXISTS update_user_updated_at ON "user";

-- Remove foreign key constraint from transaction table (if exists)
-- ALTER TABLE transaction
--     DROP CONSTRAINT IF EXISTS fk_transaction_user_id;

-- Drop index
DROP INDEX IF EXISTS idx_user_email;

-- Drop table
DROP TABLE IF EXISTS "user";
