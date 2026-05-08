# Live Platform Lab — Claude Code 가이드

## 프로젝트 개요

AI 기반 라이브 커머스 플랫폼. 실시간 채팅·RAG Q&A·LLM 인사이트를 직접 설계·구현한 포트폴리오 프로젝트.

## 모듈 구조

```
live-platform-lab/
├── live-commerce-core/   # 핵심 도메인 (Hexagonal Architecture)
├── live-commerce-api/    # REST API 서버 (포트 8090)
├── live-chat-server/     # WebSocket/STOMP 채팅 서버 (포트 8080)
└── web-client/           # Next.js 14 프론트엔드 (포트 3000)
```

각 모듈의 상세 내용은 해당 디렉토리의 CLAUDE.md를 참고할 것.

## 기술 스택

- **Language**: Java 25, TypeScript
- **Framework**: Spring Boot 4.0, Next.js 14
- **Infra**: MySQL 8, MongoDB 7, Redis 7, Elasticsearch 9
- **Real-time**: STOMP over SockJS, Redis Pub/Sub
- **AI**: OpenAI GPT-4o-mini, Spring AI, RAG

## 개발 워크플로우

### 브랜치 전략

```
main          ← 프로덕션
develop       ← 통합 브랜치
feature/#{이슈번호}   ← 기능 개발
```

### 커밋 컨벤션

```
[#{이슈번호}] type: 설명

type: feat | fix | refactor | docs | chore | perf
```

### PR 흐름

1. `feature/#{N}` 브랜치 생성
2. 개발 후 `/pr-create` 로 PR 생성 (base: develop)
3. `/pr-review` 로 리뷰 → `/pr-apply` 로 반영

## Available Skills

| 명령어 | 용도 |
|--------|------|
| `/plan` | 이슈 분석 → 구현 계획 문서 생성 |
| `/issue-create` | 변경사항 분석 → GitHub 이슈 생성 |
| `/pr-create` | 현재 브랜치 커밋 분석 → PR 생성 |
| `/pr-review` | PR 코드 리뷰 → 코멘트 등록 |
| `/pr-apply` | PR 리뷰 코멘트 → 코드 반영 |
| `/question` | 질문 답변 → docs/claude/question/ 저장 |

## 빌드 & 실행

```bash
# 전체 빌드
./gradlew build

# 모듈별 컴파일
./gradlew :live-chat-server:compileJava
./gradlew :live-commerce-core:compileJava
./gradlew :live-commerce-api:compileJava

# 인프라
make infra-up          # Redis, MySQL, MongoDB, Elasticsearch
docker compose -f docker-compose.monitoring.yml up -d  # Prometheus, Grafana

# 코드 포맷 (Spotless)
./gradlew spotlessApply
```

## 코드 품질 규칙

- **포맷터**: Spotless (Google Java Format) — PR 전 반드시 실행
- **패키지**: `org.giglab.live.*`
- **예외**: 도메인 예외는 `*DomainException`, 에러코드는 `*ErrorCode` enum
- **트랜잭션**: 외부 HTTP 호출은 트랜잭션 밖에서 수행

## 알려진 패턴 & 주의사항

### Redis Pub/Sub → STOMP 브로드캐스트
`RoomBroadcastPublisher`가 Redis에 JSON 문자열을 발행하면,
`RoomBroadcastSubscriber`가 수신해 STOMP로 전달한다.
**주의**: `SimpMessagingTemplate.convertAndSend`에 `JsonNode` 객체를 넘기면
JSON 콘텐츠가 아닌 클래스 getter 결과가 직렬화된다 → 반드시 `body`(문자열)를 전달.

### ActionResponse 다형성
`@JsonTypeInfo(property = "action", visible = true)` + `@JsonSubTypes`로
`DefaultActionResponse` 하나가 여러 action 이름에 매핑된다.
직렬화 시 실제 `action` 필드값이 사용되므로 정상 동작함.

### chatRoomId 생명주기
캠페인 시작 시 채팅방 생성이 선행 조건 (PR #110).
`campaign.chatRoomId`가 null이면 WebSocket 연결 불가.
