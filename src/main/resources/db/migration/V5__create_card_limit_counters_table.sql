-- 5. CARD LIMIT COUNTERS TABLE
-- Tracks real-time spending against defined limits. 
-- Separated from configs for performance and concurrency management.
CREATE TABLE card_limit_counters
(
    id             UUID PRIMARY KEY,
    card_id        UUID        NOT NULL,
    limit_type     VARCHAR(50) NOT NULL,
    current_amount NUMERIC(19, 4) DEFAULT 0.0000,
    reset_date     TIMESTAMPTZ NOT NULL, -- When the counter expires (e.g., end of day)

    CONSTRAINT uk_card_limit_counters_type UNIQUE (card_id, limit_type),
    CONSTRAINT fk_limit_counters_card_id FOREIGN KEY (card_id) REFERENCES cards (id)
);
CREATE INDEX idx_card_limit_counters_card_id ON card_limit_counters (card_id);
