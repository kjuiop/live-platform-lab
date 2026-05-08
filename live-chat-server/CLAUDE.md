# live-chat-server — Claude Code 가이드

## 역할

WebSocket/STOMP 기반 실시간 채팅 서버. Redis Pub/Sub으로 멀티 인스턴스 확장.

- **포트**: 8080
- **패키지**: `org.giglab.live`

## 패키지 구조

```
src/main/java/org/giglab/live/
├── application/
│   ├── command/              # ActionDispatcher + ActionHandler 구현체
│   │   ├── chat/             # SendMessage, JoinRoom, LeaveRoom, FaqQuestion
│   │   └── banner/           # ProductBannerOn, ProductBannerOff
│   ├── dto/
│   │   ├── action/           # ActionRequest, ActionResponse, DefaultActionResponse...
│   │   └── ChatMessageResponse.java
│   ├── service/              # ChatMessageService, FaqAnswerService, ViewerSessionService
│   └── port/                 # 포트 인터페이스 (persistence, messaging, ai)
├── domain/
│   └── model/                # ChatMessage, Room (도메인 엔티티)
├── infrastructure/
│   ├── adapter/              # RoomBroadcastPublisher, HttpChatRoomAdapter
│   ├── redis/                # RedisRoomRepository, RedisPubSubChannel
│   ├── mongodb/              # ChatMessageRepository
│   └── ai/                   # FaqAnswerAdapter (OpenAI 연동)
└── presentation/
    ├── api/v1/controller/
    │   ├── websocket/        # RoomActionController (@MessageMapping)
    │   └── RoomController, ChatMessageController (REST)
    └── subscriber/           # RoomBroadcastSubscriber (Redis → STOMP)
```

## 메시지 흐름

```
클라이언트 SEND /send/room.action
  → RoomActionController.handle(ActionRequest)
  → ActionDispatcher.dispatch() → ActionHandler.execute()
  → RoomActionFacade: assignSeq → publish → saveIfNeeded
  → RoomBroadcastPublisher → Redis channel "room:{roomId}"
  → RoomBroadcastSubscriber.onMessage()
  → SimpMessagingTemplate → /sub/room/{roomId}
  → 클라이언트 수신
```

## Action 타입

`ActionType` enum에 정의. key 값이 실제 전송되는 문자열.

| ActionType | key | 저장 여부 |
|-----------|-----|----------|
| CHAT_MESSAGE | CHAT.MESSAGE | ✅ MongoDB |
| FAQ_QUESTION | FAQ.QUESTION | ✅ MongoDB |
| FAQ_ANSWER | FAQ.ANSWER | ✅ MongoDB |
| FAQ_ERROR | FAQ.ERROR | ✅ MongoDB |
| CHAT_JOIN | CHAT.JOIN | ❌ |
| CHAT_LEAVE | CHAT.LEAVE | ❌ |
| VIEWER_COUNT | VIEWER.COUNT | ❌ |
| PRODUCT_BANNER_ON | PRODUCT.BANNER.ON | ❌ |
| PRODUCT_BANNER_OFF | PRODUCT.BANNER.OFF | ❌ |

## 핵심 클래스

- `ActionDispatcher`: action 문자열 → `ActionHandler` O(1) 매핑
- `DefaultActionResponse`: 대부분의 메시지에 사용하는 응답 DTO (sealed interface 구현체)
- `ChatMessageService.assignSeq()`: Redis INCR으로 seq 부여 (메시지 유실 감지용)
- `RoomBroadcastSubscriber.recoverIfGap()`: seq gap 감지 시 MongoDB에서 복구 메시지 재전송

## WebSocket 설정

- STOMP endpoint: `/ws` (SockJS fallback 포함)
- Subscribe prefix: `/sub`
- Send prefix: `/send`
- Heartbeat: 5초

## 빌드 & 테스트

```bash
./gradlew :live-chat-server:compileJava
./gradlew :live-chat-server:test
./gradlew :live-chat-server:test --tests "org.giglab.live.application.command.chat.*"
```

## 주의사항

- `RoomBroadcastSubscriber`에서 `convertAndSend`는 반드시 `body`(JSON 문자열) 사용
  (`JsonNode` 객체를 넘기면 getter 직렬화 버그 발생)
- `saveIfNeeded`는 `@Async`로 비동기 처리 — 트랜잭션 없음
- seq가 0인 메시지는 MongoDB에 저장하지 않음 (guard 존재)
