/**
 * CloudFront Function: Next.js Static Export Path Rewriter
 * 
 * 이 함수는 Next.js 정적 내보내기에서 생성된 페이지 경로를 자동으로 처리합니다.
 * trailingSlash: true 설정으로 빌드된 페이지들(/stomp-test/index.html 등)을
 * 슬래시 없는 경로(/stomp-test)로도 접근할 수 있게 합니다.
 * 
 * CloudFront 설정 방법:
 * 1. AWS Console > CloudFront > Functions
 * 2. "Create function" 클릭
 * 3. Function name: "nextjs-path-rewriter" (또는 원하는 이름)
 * 4. 아래 코드를 복사하여 붙여넣기
 * 5. "Deploy" 클릭
 * 6. Distribution의 Behaviors에서 이 함수를 "Viewer request"에 연결
 */
function handler(event) {
    var request = event.request;
    var uri = request.uri;
    
    // 이미 확장자가 있는 파일 요청 (예: .js, .css, .png 등)은 그대로 통과
    if (uri.includes('.')) {
        return request;
    }
    
    // 루트 경로(/)는 그대로
    if (uri === '/') {
        return request;
    }
    
    // 마지막이 슬래시로 끝나면 index.html 추가
    if (uri.endsWith('/')) {
        request.uri = uri + 'index.html';
        return request;
    }
    
    // 슬래시로 끝나지 않으면 슬래시 + index.html 추가
    // 예: /stomp-test → /stomp-test/index.html
    request.uri = uri + '/index.html';
    return request;
}
