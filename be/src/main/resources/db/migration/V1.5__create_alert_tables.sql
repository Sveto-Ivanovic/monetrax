CREATE TYPE alert_rule_type  AS ENUM ('LESS_OR_EQUAL', 'GREATER_OR_EQUAL', 'BETWEEN', 'EQUAL', 'LESS', 'GREATER');

CREATE TABLE spending_alerts (
                                 alert_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 user_id UUID NOT NULL REFERENCES user_table(user_id) ON DELETE CASCADE,
                                 account_id UUID REFERENCES accounts(account_id) ON DELETE CASCADE,
                                 name VARCHAR(150) NOT NULL,
                                 description VARCHAR(255),
                                 date_from DATE NOT NULL,
                                 date_to DATE NOT NULL,
                                 created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                 updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE alert_conditions (
                                  condition_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  alert_id UUID NOT NULL REFERENCES spending_alerts(alert_id) ON DELETE CASCADE,
                                  category_id UUID NOT NULL REFERENCES categories_table(category_id),
                                  category_name VARCHAR(150) NOT NULL,
                                  rule_type alert_rule_type NOT NULL,
                                  limit_value_low_or_equal NUMERIC(14,2),
                                  limit_value_high NUMERIC(14,2),
                                  UNIQUE (alert_id, category_id),
                                  CONSTRAINT chk_alert_conditions_limits
                                      CHECK (
                                          (limit_value_low_or_equal IS NULL AND limit_value_high IS NOT NULL)
                                              OR
                                          (limit_value_low_or_equal IS NOT NULL AND limit_value_high IS NULL)
                                          )
);