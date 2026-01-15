-- 2. ACCOUNTS TABLE
-- Acts as the "Source of Truth" for funds. Each account belongs to a user 
-- and holds the actual monetary balance.
CREATE TABLE accounts
(
    id           UUID PRIMARY KEY,
    user_id      UUID               NOT NULL,
    iban         VARCHAR(34) UNIQUE NOT NULL, -- IBAN standard
    currency     CHAR(3)            NOT NULL,
    balance      NUMERIC(19, 4)     NOT NULL DEFAULT 0.0000,
    account_type VARCHAR(20)        NOT NULL,
    status       VARCHAR(20)        NOT NULL,
    created_at   TIMESTAMPTZ        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMPTZ        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_accounts_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);
CREATE INDEX idx_accounts_user_id ON accounts (user_id);
