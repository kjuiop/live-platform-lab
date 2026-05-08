# live-event-collector — Claude Code 가이드

## 역할

사용자 이벤트를 HTTP로 수집해 Kafka에 발행. Vector가 Kafka → ClickHouse로 적재.

- **포트**: 8070
- **패키지**: `org.giglab.live.collector`

## 패키지 구조

```
src/main/java/org/giglab/live/collector/
├── presentation/
│   ├── controller/        # EventController (POST /api/v1/events)
│   └── dto/               # EventRequest
├── application/
│   ├── EventService
│   ├── usecase/           # SendEventUseCase
│   └── port/              # EventPublisher (인터페이스)
├── domain/
│   └── model/
│       ├── Event
│       └── types/EventType  # 이벤트 타입 enum
└── infrastructure/
    ├── adapter/           # KafkaEventPublisher
    └── kafka/
        ├── config/        # KafkaConfig
        └── EventKafkaPublisher
```

## 이벤트 흐름

```
클라이언트 → POST /api/v1/events (EventRequest)
  → EventService → SendEventUseCase
  → KafkaEventPublisher → Kafka topic "live-events"
  → Vector (Kafka consumer) → ClickHouse
  → live-analytics-api에서 집계 조회
```

## Kafka 설정

환경 변수 `KAFKA_BOOTSTRAP_SERVERS`로 브로커 주소 주입.
토픽: `live-events` (파티션 3, 복제 1)

```bash
# 토픽 생성 (최초 1회)
make kafka-init
```

## 인프라 실행

```bash
make collector-up   # Kafka + Kafka UI + ClickHouse + Vector
make collector-down
```

## 빌드

```bash
./gradlew :live-event-collector:compileJava
./gradlew :live-event-collector:test
```
