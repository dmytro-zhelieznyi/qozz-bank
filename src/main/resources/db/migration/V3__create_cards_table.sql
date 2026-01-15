-- 3. CARDS TABLE
-- Represents physical or virtual payment instruments. 
-- Cards do not hold money; they provide access to an account's balance.
CREATE TABLE cards
(
    id               UUID PRIMARY KEY,
    account_id       UUID         NOT NULL,
    masked_pan       VARCHAR(19)  NOT NULL, -- Security-safe card number display
    expiry_date      DATE         NOT NULL,
    card_holder_name VARCHAR(200) NOT NULL,
    status           VARCHAR(20)  NOT NULL,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_cards_account_id FOREIGN KEY (account_id) REFERENCES accounts (id)
);
CREATE INDEX idx_cards_account_id ON cards (account_id);
