ALTER TABLE transactions
    ADD COLUMN from_iban VARCHAR(34),
    ADD COLUMN to_iban   VARCHAR(34);

CREATE INDEX idx_transactions_created_at ON transactions (created_at);
CREATE INDEX idx_transactions_operation_id ON transactions (operation_id);
