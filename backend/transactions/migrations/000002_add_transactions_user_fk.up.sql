-- Add foreign key constraint from transaction to user
ALTER TABLE transaction
    ADD CONSTRAINT fk_transaction_user_id
    FOREIGN KEY (user_id)
    REFERENCES "user"(id)
    ON DELETE CASCADE;

COMMENT ON CONSTRAINT fk_transaction_user_id ON transaction IS 'Foreign key to user table - cascades on user deletion';
