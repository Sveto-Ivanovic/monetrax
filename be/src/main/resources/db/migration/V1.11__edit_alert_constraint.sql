ALTER TABLE alert_conditions
DROP CONSTRAINT chk_alert_conditions_limits;

ALTER TABLE alert_conditions
    ADD CONSTRAINT chk_alert_conditions_limits CHECK (
        limit_value_low_or_equal IS NOT NULL
            OR limit_value_high IS NOT NULL
        );