-- 6. TRANSACTIONS TABLE
-- The immutable ledger recording all movements of funds. 
-- Links accounts to card usage and external payment references.
CREATE TABLE transactions
(
    id              UUID PRIMARY KEY,
    operation_id    UUID           NOT NULL,
    account_id      UUID           NOT NULL,
    related_card_id UUID,
    amount          NUMERIC(19, 4) NOT NULL,
    type            VARCHAR(50)    NOT NULL,
    status          VARCHAR(20)    NOT NULL,
    description     TEXT,
    reference_id    VARCHAR(100) UNIQUE, -- ID from external payment networks (Visa/SWIFT)
    created_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_transactions_account_id FOREIGN KEY (account_id) REFERENCES accounts (id),
    CONSTRAINT fk_transactions_card_id FOREIGN KEY (related_card_id) REFERENCES cards (id)
);
CREATE INDEX idx_transactions_account_id ON transactions (account_id);
