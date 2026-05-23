ALTER TABLE accounts ADD COLUMN verified          BOOLEAN   NOT NULL DEFAULT FALSE;
ALTER TABLE accounts ADD COLUMN verification_token VARCHAR(64);
ALTER TABLE accounts ADD COLUMN token_expires_at  TIMESTAMP;

-- Grandfather in existing accounts so they are not locked out
UPDATE accounts SET verified = TRUE WHERE verification_token IS NULL;

CREATE INDEX idx_accounts_verification_token ON accounts (verification_token);
