# CloudFront Function 설정 가이드

Next.js 정적 내보내기에서 생성된 모든 페이지가 자동으로 작동하도록 CloudFront Function을 설정합니다.

## 설정 방법

### 1. CloudFront Function 생성

1. AWS Console > CloudFront > Functions 이동
2. **"Create function"** 클릭
3. Function name: `nextjs-path-rewriter` (또는 원하는 이름)
4. `cloudfront-function.js` 파일의 내용을 복사하여 붙여넣기
5. **"Deploy"** 클릭

### 2. Distribution에 Function 연결

1. AWS Console > CloudFront > Distributions 이동
2. 해당 Distribution 선택
3. **"Behaviors"** 탭 클릭
4. Default behavior (또는 원하는 behavior) 선택 후 **"Edit"**
5. **"Viewer request"** 섹션에서:
   - Function type: **CloudFront Functions**
   - Function: 방금 생성한 `nextjs-path-rewriter` 선택
6. **"Save changes"** 클릭

### 3. 배포 확인

설정이 완료되면 다음 경로들이 모두 작동합니다:

- `/` → `index.html`
- `/stomp-test` → `stomp-test/index.html`
- `/stomp-test/` → `stomp-test/index.html`
- `/any-new-page` → `any-new-page/index.html` (새 페이지 추가 시 자동 작동)

## 작동 원리

`trailingSlash: true` 설정으로 Next.js가 빌드하면:
- `pages/index.tsx` → `out/index.html`
- `pages/stomp-test.tsx` → `out/stomp-test/index.html`
- `pages/new-page.tsx` → `out/new-page/index.html`

CloudFront Function이 요청을 가로채서:
- `/stomp-test` 요청 → `/stomp-test/index.html`로 변환
- `/stomp-test/` 요청 → `/stomp-test/index.html`로 변환
- 정적 파일(.js, .css 등)은 그대로 통과

## 주의사항

- Function을 배포한 후 CloudFront 캐시 무효화가 필요할 수 있습니다
- 배포 워크플로우에서 자동으로 캐시 무효화가 실행됩니다
- 새 페이지를 추가하면 자동으로 작동합니다 (추가 설정 불필요)
