# Live Platform Lab

> AI 기반 라이브 커머스 플랫폼 — 실시간 채팅 · RAG Q&A · LLM 인사이트를 직접 설계·구현한 포트폴리오 프로젝트

**라이브 커머스** 환경을 가정하고, 상품 PDF 임베딩부터 방송 중 실시간 AI 답변, 호스트용 인사이트 리포트까지 AI 기능 전 주기를 백엔드에서 직접 구현했습니다.

---

## 구현 기능 개요

| 기능 | 설명 | 기술 |
|------|------|------|
| **상품 PDF 임베딩** | PDF를 청크로 분할해 Elasticsearch 벡터 DB에 저장 | Spring AI · Elasticsearch |
| **RAG 기반 AI Q&A** | 벡터 검색으로 관련 문서를 Retrieve → LLM 답변 생성 | RAG · OpenAI |
| **방송 전 사전 Q&A 생성** | 임베딩 문서 기반 예상 질문·답변을 LLM 단일 호출로 JSON 배열 생성 → DB 저장 | Prompt Engineering |
| **실시간 채팅** | WebSocket/STOMP 기반 라이브 채팅, Redis Pub/Sub으로 멀티 인스턴스 확장 | STOMP · Redis |
| **AI 미답변 질문 집계** | 방송 중 LLM이 "정보 없음"으로 응답한 질문을 MongoDB 쿼리로 추출 → 호스트 화면 노출 | MongoDB Aggregation |
| **방송 종료 AI 리포트** | 채팅 통계·FAQ 분석 데이터를 LLM에 전달해 방송 성과 요약 리포트 생성 | LLM Summarization |
| **동시 시청자 수 집계** | Redis 기반 Presence 관리 및 실시간 브로드캐스트 | Redis · WebSocket |

---

## 아키텍처

### 전체 구조

```
┌─────────────────────────────────────────────────────────┐
│                      web-client (Next.js)               │
│   방송 상세  ·  상품 상세  ·  실시간 채팅  ·  인사이트  │
└────────────────┬───────────────────┬────────────────────┘
                 │ REST              │ WebSocket/STOMP
    ┌────────────▼──────────┐  ┌────▼──────────────────────┐
    │   live-commerce-api   │  │     live-chat-server       │
    │   (Spring Boot 4.0)   │  │     (Spring Boot 4.0)      │
    │                       │  │                            │
    │  live-commerce-core   │  │  ActionDispatcher          │
    │  (Hexagonal Arch)     │  │  FaqAnswerService          │
    │  · Product Domain     │  │  ChatMessageAggregator     │
    │  · Campaign Domain    │  │  ViewerSessionAggregator   │
    └──────┬────────────────┘  └──────┬─────────────────────┘
           │                          │
    ┌──────▼──────────────────────────▼──────────┐
    │               Infrastructure               │
    │  MySQL  ·  MongoDB  ·  Redis  ·  ES(Vector)│
    └─────────────────────────────────────────────┘
```

### 모듈 구조

```
live-platform-lab/
├── live-commerce-core/     # 핵심 도메인 (Hexagonal Architecture)
│   ├── product/            # 상품 · PDF · AI Q&A · FAQ 도메인
│   └── campaign/           # 방송 캠페인 · AI 리포트 도메인
├── live-commerce-api/      # REST API (Facade 패턴)
├── live-chat-server/       # 실시간 채팅 서버 (WebSocket/STOMP)
└── web-client/             # Next.js 프론트엔드
```

---

## AI 기능 상세

### 1. RAG 기반 상품 Q&A

방송 중 시청자의 질문을 호스트 대신 AI가 답변합니다.

```
시청자 질문 → Elasticsearch 벡터 검색 (Top-K 관련 청크 추출)
           → LLM에 [Context + 질문] 전달
           → 근거 기반 답변 생성 → 채팅방 브로드캐스트
```

- **Elasticsearch Hybrid Search**: 키워드 + 벡터 검색 결합
- 임베딩 모델: OpenAI `text-embedding-ada-002`
- LLM: OpenAI `gpt-4o-mini` (Spring AI `ChatClient`)

### 2. 방송 전 사전 Q&A 생성 (Prompt Engineering)

방송 전 셀러가 예상 질문·답변을 미리 준비할 수 있도록 LLM이 자동 생성합니다.

```java
// 단일 LLM 호출로 JSON 배열 반환 → ObjectMapper 파싱 → DB 저장
String prompt = """
    위 상품에 대해 라이브 방송 시청자가 자주 물어볼 법한 질문 5개와
    각 답변을 다음 JSON 형식으로만 반환하세요:
    [{"question": "...", "answer": "..."}]
    """;
```

- LLM 호출과 DB 저장을 **트랜잭션 분리**: `GenerateProductFaqSamplesUseCase`(LLM) → `SaveProductFaqSamplesUseCase`(@Transactional)
- 누적 저장 방식 — 방송마다 Q&A를 추가 생성

### 3. AI 미답변 질문 집계

LLM이 정보 부족으로 답변하지 못한 질문을 수집해 호스트 화면에 노출합니다.

```java
// MongoDB $regex로 "정보 없음" 응답 필터링 → question 필드 추출
Query query = new Query(
    Criteria.where("action").is("FAQ_ANSWER")
        .and("payload.answer").regex("해당 정보를 찾을 수 없습니다"));
```

### 4. 방송 종료 AI 리포트

방송 종료 시 채팅 통계 + FAQ 지표를 LLM에 전달해 성과 요약 리포트를 자동 생성합니다.

```
채팅 메시지 수 · FAQ 질문 수 · 미답변 수 · 동시 시청자 피크
→ LLM 프롬프트 조합 → 방송 성과 요약 텍스트 생성 → DB 저장
```

---

## 기술 스택

### Backend

| 분류 | 기술 |
|------|------|
| Language | Java 25 |
| Framework | Spring Boot 4.0, Spring AI |
| Real-time | Spring WebSocket, STOMP, Redis Pub/Sub |
| ORM | Spring Data JPA, QueryDSL, Spring Data MongoDB |
| AI/LLM | OpenAI GPT-4o-mini, Spring AI ChatClient |
| Vector Search | Elasticsearch (dense_vector) |
| Architecture | Hexagonal Architecture (Ports & Adapters) |
| Code Quality | Spotless (Google Java Format), Checkstyle |

### Data

| 역할 | 기술 |
|------|------|
| 상품·캠페인 저장 | MySQL 8.0 |
| 채팅 메시지·세션 | MongoDB 7 |
| 룸 상태·Presence | Redis 7 |
| 벡터 임베딩 | Elasticsearch 9 |

### Frontend & Infra

| 분류 | 기술 |
|------|------|
| Frontend | Next.js 14, React 18, TypeScript |
| Container | Docker, Docker Compose |

---

## 실행 방법

```bash
# 인프라 (Redis, MySQL, MongoDB, Elasticsearch)
docker compose -f docker-compose.infra.yml up -d

# 모니터링 (Prometheus, Grafana, Redis Exporter)
docker compose -f docker-compose.infra.yml -f docker-compose.monitoring.yml up -d

# 채팅 서버
docker compose -f docker-compose.infra.yml -f docker-compose.chat.yml up -d

# 커머스 API
docker compose -f docker-compose.infra.yml -f docker-compose.api.yml up -d

# 이벤트 수집 (Kafka, ClickHouse, Vector)
docker compose -f docker-compose.infra.yml -f docker-compose.collector.yml up -d

# 전체 스택
docker compose -f docker-compose.infra.yml -f docker-compose.monitoring.yml -f docker-compose.chat.yml -f docker-compose.api.yml up -d
```

### 이미지 빌드

```bash
docker build -f Dockerfile -t kjuiop/live-chat-server:latest .
docker build -f Dockerfile.api -t kjuiop/live-platform-api:latest .
```

### 접속

| 서비스 | URL |
|--------|-----|
| 프론트엔드 | http://localhost:3000 |
| 채팅 서버 API | http://localhost:8080 |
| 커머스 API (Swagger) | http://localhost:8090/swagger-ui.html |

---