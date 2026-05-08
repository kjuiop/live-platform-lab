# web-client — Claude Code 가이드

## 역할

Next.js 14 (Pages Router) 기반 프론트엔드. 방송 관리·실시간 채팅·상품 관리 UI 제공.

- **포트**: 3000
- **런타임**: Node.js, TypeScript

## 페이지 구조

```
pages/
├── broadcasts/
│   ├── index.tsx          # 방송 목록
│   └── [id].tsx           # 방송 상세 (채팅·FAQ·AI리포트) ← 핵심 파일
├── products/
│   ├── index.tsx          # 상품 목록
│   └── [id].tsx           # 상품 상세
└── chat.tsx               # 채팅 데모 (다중 유저 시뮬레이션)
```

## API 연결

```typescript
// broadcasts/[id].tsx 상단
const API_BASE   = process.env.NEXT_PUBLIC_API_URL    ?? 'http://localhost:8090/api/v1'  // 커머스 API
const CHAT_API_BASE = process.env.NEXT_PUBLIC_CHAT_URL ?? 'http://localhost:8080/api/v1' // 채팅 REST
const WS_BASE_URL   = process.env.NEXT_PUBLIC_WS_URL  ?? 'http://localhost:8080'         // STOMP
```

## WebSocket (STOMP) 패턴 — broadcasts/[id].tsx

SockJS + STOMP.js를 CDN으로 동적 로드 (Next.js Script 태그).

### 연결 흐름

```
areScriptsReady && campaign.chatRoomId && status=live
  → connect(roomId)
  → client.subscribe('/sub/room/{roomId}', msg => appendMessage(msg.body))
  → publishJoin()
  → loadHistory()  ← REST API로 이전 메시지 조회
```

### 메시지 타입 → UI 탭 매핑

| action | msgType | 표시 탭 |
|--------|---------|---------|
| CHAT.MESSAGE | 'chat' | 💬 채팅 |
| FAQ.QUESTION | 'faq-question' | 🤖 FAQ |
| FAQ.ANSWER | 'faq-answer' | 🤖 FAQ |
| FAQ.ERROR | 'faq-error' | 🤖 FAQ |
| VIEWER.COUNT | — | 시청자 수 (상태만) |
| PRODUCT.BANNER.ON/OFF | — | 배너 표시 (상태만) |
| CHAT.JOIN | — | 무시 (activeBanner 체크만) |

### 중요 refs

| ref | 용도 |
|-----|------|
| `clientRef` | STOMP client 인스턴스 |
| `subRef` | STOMP subscription |
| `seenKeysRef` | 메시지 중복 제거 (rawBody 기준, max 200) |
| `isConnectingRef` | 연결 중 플래그 (이중 연결 방지) |
| `historyLoadedRoomsRef` | 히스토리 로드 완료 roomId 추적 |
| `isAtChatBottomRef` | 채팅 컨테이너 바닥 여부 |
| `isAtFaqBottomRef` | FAQ 컨테이너 바닥 여부 |

## ChatQnAPanel 컴포넌트

`messages` 전체를 받아 내부에서 useMemo로 필터링.

```typescript
const chatMessages = useMemo(
  () => messages.filter((m) => !m.msgType || m.msgType === 'chat'),
  [messages]
);
const faqMessages = useMemo(
  () => messages.filter((m) => ['faq-question','faq-answer','faq-error'].includes(m.msgType!)),
  [messages]
);
```

### 스크롤 정책

- 채팅/FAQ 모두 `isAtBottom` ref로 바닥 여부 체크 후 자동 스크롤
- 탭 전환 시에는 자동 스크롤 없음 (`prevTabRef`로 탭 변경 감지)
- useEffect dependency: `faqMessages.length` (배열 참조 변경이 아닌 실제 증가 시만)

## 주의사항

- `sendMessage`는 STOMP 전송 성공 시 로컬 상태에 추가하지 않음 (서버 echo 수신 후 추가됨)
- `loadHistory`는 REST API 응답의 `json.data` 배열을 사용 (`json.data ?? []`)
- 히스토리 메시지 id는 MongoDB ObjectId (string), 실시간 메시지 id는 `Date.now() + Math.random()`

## 실행

```bash
cd web-client
npm install
npm run dev
```
