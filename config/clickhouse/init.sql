CREATE DATABASE IF NOT EXISTS analytics;

DROP TABLE IF EXISTS analytics.events;

CREATE TABLE IF NOT EXISTS analytics.events
(
    -- 기본 식별자
    event_id        String,
    event_type      LowCardinality(String),
    occurred_at     DateTime64(3, 'UTC'),

    -- 세션 / 사용자
    session_id      String,
    user_id         Nullable(String),
    user_name       Nullable(String),
    device_id       Nullable(String),

    -- 방송
    room_id         String,

    -- 환경
    os_type         LowCardinality(String)          DEFAULT '',
    app_version     LowCardinality(Nullable(String)),

    -- 마케팅
    -- 출처 (예: google, facebook, email 등)
    utm_source      LowCardinality(Nullable(String)),
    -- 캠페인 (예: summer_sale, black_friday 등)
    utm_campaign    LowCardinality(Nullable(String)),
    -- 매체 (예: banner, email 등)
    utm_medium      LowCardinality(Nullable(String)),

    -- 네트워크
    ip              String                          DEFAULT '',

    -- 이벤트별 가변 데이터
    properties      String                          DEFAULT '{}'
)
ENGINE = ReplacingMergeTree(occurred_at)
PARTITION BY toYYYYMM(occurred_at)
ORDER BY (room_id, event_type, occurred_at, event_id);
