CREATE TABLE character_cache (
    id             BIGSERIAL PRIMARY KEY,
    username       VARCHAR(255) NOT NULL UNIQUE,
    last_fetched   TIMESTAMP,
    cached_response TEXT
);
