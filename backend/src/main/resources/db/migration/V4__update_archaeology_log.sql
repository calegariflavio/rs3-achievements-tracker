ALTER TABLE archaeology_log
    ALTER COLUMN artifact_id DROP NOT NULL;

ALTER TABLE archaeology_log
    ADD COLUMN IF NOT EXISTS dig_site VARCHAR(255);
