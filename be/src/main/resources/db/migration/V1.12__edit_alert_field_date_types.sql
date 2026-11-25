BEGIN;

ALTER TABLE spending_alerts
ALTER COLUMN date_from TYPE TIMESTAMPTZ
        USING (date_from::timestamp AT TIME ZONE 'UTC'),
    ALTER COLUMN date_to TYPE TIMESTAMPTZ
        USING ((date_to::timestamp + TIME '23:59:59') AT TIME ZONE 'UTC');

COMMIT;