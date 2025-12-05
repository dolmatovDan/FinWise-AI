ALTER TABLE transaction
    ADD CONSTRAINT fk_transaction_category_id
    FOREIGN KEY (category_id)
    REFERENCES "category"(id)
    ON DELETE CASCADE;

COMMENT ON CONSTRAINT fk_transaction_category_id ON transaction IS 'Foreign key to category table - cascades on category deletion';
