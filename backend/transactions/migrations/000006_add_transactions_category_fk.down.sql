-- Remove foreign key constraint from transaction to category
ALTER TABLE transaction
    DROP CONSTRAINT IF EXISTS fk_transaction_category_id;
