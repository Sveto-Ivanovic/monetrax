CREATE TABLE transaction_recurrence_rules (
                                              recurrence_rule_id     UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                                              source_transaction_id  UUID NOT NULL,
                                              recurrence_unit        VARCHAR(10) NOT NULL
                                                  CHECK (recurrence_unit IN ('DAY', 'WEEK', 'MONTH', 'YEAR')),
                                              interval_count         INT NOT NULL,
                                              last_run_date          DATE,
                                              max_occurrences        INT NOT NULL,
                                              occurrences_generated  INT DEFAULT 0 NOT NULL,
                                              next_run_date          DATE NOT NULL,
                                              CONSTRAINT fk_recurrence_source_transaction
                                                  FOREIGN KEY (source_transaction_id) REFERENCES transactions (transaction_id)
);