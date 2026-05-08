# live-commerce-api — Claude Code 가이드

## 역할

상품·카테고리·캠페인 REST API 서버. `live-commerce-core` 도메인을 조합해 클라이언트에 노출.

- **포트**: 8090
- **패키지**: `org.giglab.live.commerce.api`
- **Swagger**: http://localhost:8090/swagger-ui

## 패키지 구조

```
src/main/java/org/giglab/live/commerce/api/
├── controller/
│   ├── ProductController     # 상품 CRUD, PDF 임베딩, FAQ 생성, Q&A
│   ├── CategoryController    # 카테고리 트리
│   └── CampaignController    # 방송 캠페인 관리, 방송 시작/종료
├── dto/
│   ├── product/              # ProductRequest, ProductResponse
│   ├── category/             # CategoryRequest, CategoryResponse
│   ├── campaign/             # CampaignRequest, CampaignResponse, BroadcastStatusResponse
│   └── pagination/           # CursorPagination, OffsetPagination
├── mapper/                   # MapStruct 매퍼 (Result ↔ Response 변환)
│   ├── ProductMapper
│   ├── CategoryMapper
│   ├── CampaignMapper
│   └── CampaignReportMapper
├── facade/                   # UseCase 조합 파사드
└── config/
```

## DTO 변환 규칙

**core Result DTO → api Response DTO** 변환은 반드시 MapStruct 매퍼 사용.

```java
// 올바른 패턴
GetCampaignResult result = campaignService.getCampaign(id);
return ApiResponse.success(campaignMapper.toResponse(result));
```

엔티티를 직접 컨트롤러에 노출하지 않는다.

## 응답 형식

```java
public class ApiResponse<T> {
  private final T data;
  private final ErrorResponse error;
}
```

성공: `{ "data": ..., "error": null }`
실패: `{ "data": null, "error": { "code": "...", "message": "..." } }`

## 주요 API 엔드포인트

| Method | Path | 설명 |
|--------|------|------|
| GET | /campaigns/{id} | 캠페인 상세 (chatRoomId 포함) |
| POST | /campaigns/{id}/start | 방송 시작 → 채팅방 생성 후 ON_AIR |
| POST | /campaigns/{id}/end | 방송 종료 → ENDED |
| POST | /campaigns/{id}/reports | AI 리포트 조회/생성 |
| GET | /products/{id}/faq-samples | 사전 Q&A 목록 |

## 빌드

```bash
./gradlew :live-commerce-api:compileJava
./gradlew :live-commerce-api:test
```
