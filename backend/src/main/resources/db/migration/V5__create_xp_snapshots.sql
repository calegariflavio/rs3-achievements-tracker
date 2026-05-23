CREATE TABLE xp_snapshots (
    id         BIGSERIAL    PRIMARY KEY,
    username   VARCHAR(64)  NOT NULL,
    skill_id   INT          NOT NULL,
    xp         BIGINT       NOT NULL,
    recorded_at TIMESTAMP   NOT NULL
);

CREATE INDEX idx_xp_snap_user_time ON xp_snapshots (username, recorded_at);
