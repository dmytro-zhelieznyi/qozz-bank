-- 4. CARD LIMIT CONFIGS TABLE
-- Defines business rules and spending restrictions for each card (e.g., Daily limits).
CREATE TABLE card_limit_configs
(
    id         UUID PRIMARY KEY,
    card_id    UUID           NOT NULL,
    limit_type VARCHAR(50)    NOT NULL,
    max_amount NUMERIC(19, 4) NOT NULL,
    is_active  BOOLEAN        NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_card_limit_configs_type UNIQUE (card_id, limit_type),
    CONSTRAINT fk_card_limit_configs_card_id FOREIGN KEY (card_id) REFERENCES cards (id)
);
CREATE INDEX idx_card_limit_configs_card_id ON card_limit_configs (card_id);
