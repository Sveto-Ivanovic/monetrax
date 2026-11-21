ALTER TABLE transaction_recurrence_rules
    ADD COLUMN IF NOT EXISTS recurrence_unit VARCHAR(10) NOT NULL DEFAULT 'DAY';

ALTER TABLE transaction_recurrence_rules
    ALTER COLUMN recurrence_unit DROP DEFAULT;

ALTER TABLE transaction_recurrence_rules
    ADD CONSTRAINT chk_recurrence_unit
        CHECK (recurrence_unit IN ('DAY', 'WEEK', 'MONTH', 'YEAR'));


UPDATE transaction_recurrence_rules
SET max_occurrences = 1
WHERE max_occurrences IS NULL;

ALTER TABLE transaction_recurrence_rules
    ALTER COLUMN max_occurrences SET NOT NULL;

CREATE INDEX IF NOT EXISTS idx_recurrence_source_transaction
    ON transaction_recurrence_rules (source_transaction_id);