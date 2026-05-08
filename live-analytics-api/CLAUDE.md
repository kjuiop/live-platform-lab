# live-analytics-api — Claude Code 가이드

## 역할

라이브 커머스 이벤트 분석 API. ClickHouse OLAP DB 기반으로 퍼널·UTM·시청자 지표 조회.

- **포트**: 8060
- **패키지**: `org.giglab.live.analytics.api`

## 패키지 구조

```
src/main/java/org/giglab/live/analytics/api/
├── presentation/
│   ├── controller/        # AnalyticsController
│   └── dto/               # FunnelResponse, UtmResponse, ViewerResponse
├── application/
│   ├── AnalyticsService
│   ├── usecase/           # GetFunnelUseCase, GetUtmUseCase, GetViewerUseCase
│   ├── dto/               # GetFunnelResult, GetUtmResult, GetViewerResult, DateRange
│   └── port/persistence/  # AnalyticsQueryPort (인터페이스)
└── infrastructure/
    ├── adapter/           # ClickHouseAnalyticsQueryAdapter
    └── clickhouse/        # FunnelClickHouseRepository, UtmClickHouseRepository, ViewerClickHouseRepository
```

## 아키텍처

Hexagonal Architecture. ClickHouse 쿼리는 `AnalyticsQueryPort` 인터페이스를 통해서만 접근.

```
AnalyticsController
  → AnalyticsService (UseCase 조합)
    → AnalyticsQueryPort (포트)
      → ClickHouseAnalyticsQueryAdapter (어댑터)
        → ClickHouse JDBC
```

## 데이터 소스

- **ClickHouse**: OLAP 이벤트 데이터 (live-event-collector가 Kafka → Vector → ClickHouse로 적재)
- `live-event-collector`가 수집한 이벤트를 집계해 분석

## 빌드

```bash
./gradlew :live-analytics-api:compileJava
./gradlew :live-analytics-api:test
```
