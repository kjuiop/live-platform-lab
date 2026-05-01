CREATE DATABASE IF NOT EXISTS analytics;

CREATE TABLE IF NOT EXISTS analytics.events
(
    event_id     String,
    event_type   LowCardinality(String),
    occurred_at  DateTime64(3, 'UTC'),
    session_id   String,
    user_id      Nullable(String),
    room_id      String,
    properties   String
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(occurred_at)
ORDER BY (room_id, event_type, occurred_at);
