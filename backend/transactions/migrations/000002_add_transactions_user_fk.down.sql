-- Remove foreign key constraint from transaction to user
ALTER TABLE transaction
    DROP CONSTRAINT IF EXISTS fk_transaction_user_id;
