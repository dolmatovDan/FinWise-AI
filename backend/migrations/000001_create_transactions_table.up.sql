-- Create transactions table
CREATE TABLE IF NOT EXISTS transactions (
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
CREATE INDEX idx_transactions_user_id ON transactions(user_id);
CREATE INDEX idx_transactions_category ON transactions(category);
CREATE INDEX idx_transactions_created_at ON transactions(created_at DESC);

-- Create function to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger to update updated_at on row update
CREATE TRIGGER update_transactions_updated_at
    BEFORE UPDATE ON transactions
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comment on table and columns for documentation
COMMENT ON TABLE transactions IS 'Stores financial transactions (income and expenses)';
COMMENT ON COLUMN transactions.id IS 'Unique transaction identifier';
COMMENT ON COLUMN transactions.user_id IS 'Reference to user (for future JWT authentication)';
COMMENT ON COLUMN transactions.amount IS 'Transaction amount (always positive)';
COMMENT ON COLUMN transactions.category IS 'Transaction category (e.g., food, transport, salary)';
COMMENT ON COLUMN transactions.description IS 'Optional transaction description';
COMMENT ON COLUMN transactions.type IS 'Transaction type: income or expense';
COMMENT ON COLUMN transactions.created_at IS 'Timestamp when transaction was created';
COMMENT ON COLUMN transactions.updated_at IS 'Timestamp when transaction was last updated';
