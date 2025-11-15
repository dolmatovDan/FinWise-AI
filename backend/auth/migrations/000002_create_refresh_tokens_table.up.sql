-- Create refresh_token table for JWT refresh token management
CREATE TABLE IF NOT EXISTS refresh_token (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Create indexes for better query performance
CREATE INDEX idx_refresh_token_user_id ON refresh_token(user_id);
CREATE INDEX idx_refresh_token_token_hash ON refresh_token(token_hash);
CREATE INDEX idx_refresh_token_expires_at ON refresh_token(expires_at);

-- Add foreign key constraint to user
ALTER TABLE refresh_token
    ADD CONSTRAINT fk_refresh_token_user_id
    FOREIGN KEY (user_id)
    REFERENCES "user"(id)
    ON DELETE CASCADE;

-- Comment on table and columns for documentation
COMMENT ON TABLE refresh_token IS 'Stores JWT refresh tokens for authentication';
COMMENT ON COLUMN refresh_token.id IS 'Unique refresh token record identifier';
COMMENT ON COLUMN refresh_token.user_id IS 'Reference to user who owns this token';
COMMENT ON COLUMN refresh_token.token_hash IS 'SHA256 hash of the refresh token';
COMMENT ON COLUMN refresh_token.expires_at IS 'Token expiration timestamp';
COMMENT ON COLUMN refresh_token.created_at IS 'Timestamp when token was created';
