CREATE TABLE spending_alerts (
                                 alert_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                                 user_id UUID NOT NULL,
                                 account_id UUID,
                                 name VARCHAR(150) NOT NULL,
                                 description VARCHAR(255),
                                 date_from DATE NOT NULL,
                                 date_to DATE NOT NULL,
                                 created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 CONSTRAINT fk_spending_alerts_user FOREIGN KEY (user_id) REFERENCES user_table(user_id) ON DELETE CASCADE,
                                 CONSTRAINT fk_spending_alerts_account FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
);

CREATE TABLE alert_conditions (
                                  condition_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                                  alert_id UUID NOT NULL,
                                  category_id UUID NOT NULL,
                                  rule_type ENUM('LESS_THAN', 'GREATER_THAN', 'BETWEEN', 'EQUAL', 'LESS', 'GREATER') NOT NULL,
                                  limit_value_low_or_equal NUMERIC(14,2) NOT NULL,
                                  limit_value_high NUMERIC(14,2),
                                  CONSTRAINT fk_alert_conditions_alert FOREIGN KEY (alert_id) REFERENCES spending_alerts(alert_id) ON DELETE CASCADE,
                                  CONSTRAINT fk_alert_conditions_category FOREIGN KEY (category_id) REFERENCES categories_table(category_id),
                                  CONSTRAINT uq_alert_conditions_alert_category UNIQUE (alert_id, category_id)
);
