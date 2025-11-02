-- Create user table (for future JWT authentication)
CREATE TABLE IF NOT EXISTS "user" (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Create index for email lookups
CREATE INDEX idx_user_email ON "user"(email);

-- TODO: Add foreign key constraint later when implementing JWT authentication
-- ALTER TABLE transaction
--     ADD CONSTRAINT fk_transaction_user_id
--     FOREIGN KEY (user_id)
--     REFERENCES "user"(id)
--     ON DELETE CASCADE;

-- Create trigger to update updated_at on row update
CREATE TRIGGER update_user_updated_at
    BEFORE UPDATE ON "user"
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comment on table and columns
COMMENT ON TABLE "user" IS 'Stores user accounts for authentication';
COMMENT ON COLUMN "user".id IS 'Unique user identifier';
COMMENT ON COLUMN "user".email IS 'User email (unique)';
COMMENT ON COLUMN "user".password_hash IS 'Bcrypt hashed password';
COMMENT ON COLUMN "user".full_name IS 'User full name';
COMMENT ON COLUMN "user".created_at IS 'Timestamp when user was created';
COMMENT ON COLUMN "user".updated_at IS 'Timestamp when user was last updated';
