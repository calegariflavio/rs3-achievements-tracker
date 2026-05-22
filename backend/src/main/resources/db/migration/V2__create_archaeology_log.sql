CREATE TABLE archaeology_log (
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(255) NOT NULL,
    artifact_id     VARCHAR(255) NOT NULL,
    artifact_name   VARCHAR(255),
    collection_name VARCHAR(255),
    collected_at    TIMESTAMP
);

CREATE INDEX idx_archaeology_log_username ON archaeology_log (LOWER(username));
