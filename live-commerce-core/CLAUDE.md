# live-commerce-core — Claude Code 가이드

## 역할

상품·캠페인 핵심 도메인. Hexagonal Architecture (Ports & Adapters) 적용.
`live-commerce-api`가 이 모듈을 의존한다.

- **패키지**: `org.giglab.live.commerce.core`

## 패키지 구조

```
src/main/java/org/giglab/live/commerce/core/
├── campaign/
│   ├── application/
│   │   ├── CampaignService.java          # 퍼사드 (UseCase 조합)
│   │   ├── dto/                          # Result DTO (GetCampaignResult 등)
│   │   ├── port/
│   │   │   ├── persistence/              # CampaignStorePort, CampaignQueryPort
│   │   │   └── external/                 # ChatRoomCreatePort, ChatRoomDeletePort
│   │   └── usecase/                      # 단일 책임 UseCase (각각 @Transactional)
│   │       ├── StartCampaignUseCase      # 방송 시작 + chatRoomId 저장
│   │       ├── EndCampaignUseCase
│   │       └── ...
│   └── domain/
│       ├── entity/
│       │   ├── Campaign.java             # JPA 엔티티 (chatRoomId 포함)
│       │   └── types/BroadcastStatusType # SCHEDULED | ON_AIR | ENDED
│       └── exception/
│           ├── CampaignDomainException
│           └── CampaignErrorCode         # enum
└── product/
    ├── application/                      # 상품·PDF·FAQ 유사 구조
    └── domain/
```

## 도메인 규칙

### Campaign 생명주기

```
SCHEDULED → ON_AIR → ENDED
```

- `campaign.start()`: 상태를 ON_AIR로 변경
- `campaign.assignChatRoom(roomId)`: chatRoomId 저장
- `campaign.end()`: 상태를 ENDED로 변경, chatRoomId null 처리

### 트랜잭션 분리 원칙

외부 HTTP 호출(`ChatRoomCreatePort`)은 트랜잭션 밖에서 먼저 수행 후,
성공하면 DB 트랜잭션 시작.

```java
// CampaignService.start()
String roomId = chatRoomCreatePort.createRoom(title); // 트랜잭션 밖
return startCampaignUseCase.execute(campaignId, roomId); // @Transactional
```

## UseCase 작성 규칙

- 클래스 하나 = 책임 하나
- `@Service @Transactional`
- `CampaignStorePort.findEntityById()`로 엔티티 조회 후 도메인 메서드 호출
- 결과는 Result DTO로 반환 (엔티티 직접 노출 금지)

## 예외 처리

```java
throw new CampaignDomainException(CampaignErrorCode.NOT_FOUND, "메시지");
throw new CampaignDomainException(CampaignErrorCode.CHAT_ROOM_CREATE_FAILED, cause);
```

## 빌드

```bash
./gradlew :live-commerce-core:compileJava
./gradlew :live-commerce-core:test
```
