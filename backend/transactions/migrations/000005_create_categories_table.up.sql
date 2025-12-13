CREATE TABLE IF NOT EXISTS category (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

COMMENT ON TABLE category IS 'Table for categories (e.g., food, transport, salary)';
COMMENT ON COLUMN category.id IS 'ID of a category (integer, primary key)';
COMMENT ON COLUMN category.name IS 'Name of a category for convenient display';
COMMENT ON COLUMN category.description IS 'Description of a category (optional)';
