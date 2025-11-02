-- Create transaction table
CREATE TABLE IF NOT EXISTS transaction (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id BIGINT NOT NULL,
    amount NUMERIC(15, 2) NOT NULL CHECK (amount > 0),
    category VARCHAR(100) NOT NULL,
    description TEXT,
    type VARCHAR(20) NOT NULL CHECK (type IN ('income', 'expense')),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Create indexes for better query performance
CREATE INDEX idx_transaction_user_id ON transaction(user_id);
CREATE INDEX idx_transaction_category ON transaction(category);
CREATE INDEX idx_transaction_created_at ON transaction(created_at DESC);

-- Create function to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger to update updated_at on row update
CREATE TRIGGER update_transaction_updated_at
    BEFORE UPDATE ON transaction
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comment on table and columns for documentation
COMMENT ON TABLE transaction IS 'Stores financial transactions (income and expenses)';
COMMENT ON COLUMN transaction.id IS 'Unique transaction identifier';
COMMENT ON COLUMN transaction.user_id IS 'Reference to user (for future JWT authentication)';
COMMENT ON COLUMN transaction.amount IS 'Transaction amount (always positive)';
COMMENT ON COLUMN transaction.category IS 'Transaction category (e.g., food, transport, salary)';
COMMENT ON COLUMN transaction.description IS 'Optional transaction description';
COMMENT ON COLUMN transaction.type IS 'Transaction type: income or expense';
COMMENT ON COLUMN transaction.created_at IS 'Timestamp when transaction was created';
COMMENT ON COLUMN transaction.updated_at IS 'Timestamp when transaction was last updated';
