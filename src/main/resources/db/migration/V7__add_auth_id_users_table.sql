ALTER TABLE users ADD COLUMN auth_id UUID NOT NULL UNIQUE;
CREATE INDEX idx_users_auth_id ON users(auth_id);