CREATE TYPE recurrence_unit AS ENUM ('DAY', 'WEEK', 'MONTH', 'YEAR');

CREATE TABLE transaction_recurrence_rules (
                                              recurrence_rule_id  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                              source_transaction_id UUID NOT NULL REFERENCES transactions(transaction_id),
                                              interval_count INT NOT NULL,
                                              last_run_date DATE,
                                              max_occurrences INT,
                                              occurrences_generated INT NOT NULL DEFAULT 0,
                                              next_run_date DATE NOT NULL
);