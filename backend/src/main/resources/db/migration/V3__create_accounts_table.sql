CREATE TABLE accounts (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE character_claims (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id     UUID         NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    character_name VARCHAR(255) NOT NULL UNIQUE,
    claimed_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_character_claims_account_id ON character_claims (account_id);
