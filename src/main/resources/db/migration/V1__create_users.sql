-- Migration: Create users table
-- Description: Creates the users table with id, name, email, and timestamp fields
CREATE TABLE users
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE
);

-- Create index on email for faster lookups
CREATE INDEX idx_users_email ON users (email);

