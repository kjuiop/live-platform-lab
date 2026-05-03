-- 퍼널 분석 테스트용 샘플 데이터 (room-001)
-- numbers(N) : 0~N-1 행을 생성하는 ClickHouse 테이블 함수
-- 기대 결과: impression 100 / click 30 / add_cart 10 / purchase 3 / cvr_pct 3.0
-- 실행: clickhouse-client --multiline < seed-funnel-events.sql

-- product.impression 100건
INSERT INTO analytics.events
SELECT
    generateUUIDv4()                                                AS event_id,
    'product.impression'                                            AS event_type,
    now() - toIntervalSecond(rand() % 3600)                        AS occurred_at,
    concat('sess-', leftPad(toString(number + 1), 3, '0'))         AS session_id,
    concat('user-', leftPad(toString(number + 1), 3, '0'))         AS user_id,
    arrayElement(['Alice','Bob','Charlie','Dave','Eve','Frank',
                  'Grace','Heidi','Ivan','Judy'], (number % 10) + 1) AS user_name,
    concat('dev-', leftPad(toString(number + 1), 3, '0'))          AS device_id,
    'room-001'                                                      AS room_id,
    arrayElement(['iOS', 'Android'], (number % 2) + 1)             AS os_type,
    arrayElement(['2.0.0', '1.9.0'], (number % 5 = 0) + 1)        AS app_version,
    arrayElement(['google', 'facebook', 'kakao'], (number % 3) + 1) AS utm_source,
    arrayElement(['summer_sale', 'black_friday'], (number % 2) + 1) AS utm_campaign,
    arrayElement(['banner', 'feed'], (number % 2) + 1)             AS utm_medium,
    concat('1.1.1.', toString(number + 1))                         AS ip,
    '{}'                                                            AS properties
FROM numbers(100);

-- product.click 30건 (sess-001 ~ sess-030)
INSERT INTO analytics.events
SELECT
    generateUUIDv4()                                                AS event_id,
    'product.click'                                                 AS event_type,
    now() - toIntervalSecond(rand() % 3600)                        AS occurred_at,
    concat('sess-', leftPad(toString(number + 1), 3, '0'))         AS session_id,
    concat('user-', leftPad(toString(number + 1), 3, '0'))         AS user_id,
    arrayElement(['Alice','Bob','Charlie','Dave','Eve','Frank',
                  'Grace','Heidi','Ivan','Judy'], (number % 10) + 1) AS user_name,
    concat('dev-', leftPad(toString(number + 1), 3, '0'))          AS device_id,
    'room-001'                                                      AS room_id,
    arrayElement(['iOS', 'Android'], (number % 2) + 1)             AS os_type,
    arrayElement(['2.0.0', '1.9.0'], (number % 5 = 0) + 1)        AS app_version,
    arrayElement(['google', 'facebook', 'kakao'], (number % 3) + 1) AS utm_source,
    arrayElement(['summer_sale', 'black_friday'], (number % 2) + 1) AS utm_campaign,
    arrayElement(['banner', 'feed'], (number % 2) + 1)             AS utm_medium,
    concat('1.1.1.', toString(number + 1))                         AS ip,
    '{"product_id":"P001"}'                                        AS properties
FROM numbers(30);

-- product.add_cart 10건 (sess-001 ~ sess-010)
INSERT INTO analytics.events
SELECT
    generateUUIDv4()                                                AS event_id,
    'product.add_cart'                                              AS event_type,
    now() - toIntervalSecond(rand() % 3600)                        AS occurred_at,
    concat('sess-', leftPad(toString(number + 1), 3, '0'))         AS session_id,
    concat('user-', leftPad(toString(number + 1), 3, '0'))         AS user_id,
    arrayElement(['Alice','Bob','Charlie','Dave','Eve','Frank',
                  'Grace','Heidi','Ivan','Judy'], (number % 10) + 1) AS user_name,
    concat('dev-', leftPad(toString(number + 1), 3, '0'))          AS device_id,
    'room-001'                                                      AS room_id,
    arrayElement(['iOS', 'Android'], (number % 2) + 1)             AS os_type,
    arrayElement(['2.0.0', '1.9.0'], (number % 5 = 0) + 1)        AS app_version,
    arrayElement(['google', 'facebook', 'kakao'], (number % 3) + 1) AS utm_source,
    arrayElement(['summer_sale', 'black_friday'], (number % 2) + 1) AS utm_campaign,
    arrayElement(['banner', 'feed'], (number % 2) + 1)             AS utm_medium,
    concat('1.1.1.', toString(number + 1))                         AS ip,
    '{"product_id":"P001"}'                                        AS properties
FROM numbers(10);

-- product.purchase 3건 (sess-001 ~ sess-003)
INSERT INTO analytics.events
SELECT
    generateUUIDv4()                                                AS event_id,
    'product.purchase'                                              AS event_type,
    now() - toIntervalSecond(rand() % 3600)                        AS occurred_at,
    concat('sess-', leftPad(toString(number + 1), 3, '0'))         AS session_id,
    concat('user-', leftPad(toString(number + 1), 3, '0'))         AS user_id,
    arrayElement(['Alice','Bob','Charlie'], number + 1)             AS user_name,
    concat('dev-', leftPad(toString(number + 1), 3, '0'))          AS device_id,
    'room-001'                                                      AS room_id,
    arrayElement(['iOS', 'Android'], (number % 2) + 1)             AS os_type,
    '2.0.0'                                                         AS app_version,
    arrayElement(['google', 'facebook', 'kakao'], (number % 3) + 1) AS utm_source,
    'summer_sale'                                                   AS utm_campaign,
    arrayElement(['banner', 'feed'], (number % 2) + 1)             AS utm_medium,
    concat('1.1.1.', toString(number + 1))                         AS ip,
    concat('{"product_id":"P001","amount":', toString(29900 + (rand() % 10) * 1000), '}') AS properties
FROM numbers(3);

-- 검증
-- SELECT
--   countIf(event_type = 'product.impression') AS impression,  -- 기대값: 100
--   countIf(event_type = 'product.click')      AS click,       -- 기대값: 30
--   countIf(event_type = 'product.add_cart')   AS add_cart,    -- 기대값: 10
--   countIf(event_type = 'product.purchase')   AS purchase,    -- 기대값: 3
--   round(countIf(event_type = 'product.purchase') * 100.0
--     / nullIf(countIf(event_type = 'product.impression'), 0), 2) AS cvr_pct  -- 기대값: 3.0
-- FROM analytics.events
-- WHERE room_id = 'room-001'
--   AND event_type IN ('product.impression','product.click','product.add_cart','product.purchase');
