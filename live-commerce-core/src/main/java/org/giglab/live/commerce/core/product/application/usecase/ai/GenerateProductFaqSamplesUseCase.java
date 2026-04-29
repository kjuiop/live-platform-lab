package org.giglab.live.commerce.core.product.application.usecase.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.commerce.core.product.application.dto.ai.FaqSampleItem;
import org.giglab.live.commerce.core.product.application.dto.ai.GenerateProductFaqSamplesResult;
import org.giglab.live.commerce.core.product.application.port.ai.SearchDocumentPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class GenerateProductFaqSamplesUseCase {

  private static final int TOP_K = 5;
  private static final int FAQ_COUNT = 5;

  private static final String SEED_QUERY = "상품 성분 사용법 주의사항 효과 부작용 보관방법 원산지";

  private static final String SYSTEM_PROMPT =
      """
      당신은 라이브 커머스 방송 사전 준비 전문가입니다.
      아래 [상품 자료]를 참고하여 라이브 방송에서 시청자가 자주 물어볼 법한 질문과 모범 답변을 생성하세요.

      [상품 자료]
      {context}
      """;

  private static final String USER_PROMPT =
      """
      위 상품에 대해 라이브 방송 시청자가 자주 물어볼 법한 질문 %d개와 각 답변을 다음 JSON 형식으로만 반환하세요.
      다른 설명 없이 JSON 배열만 출력하세요.

      [
        {"question": "질문1", "answer": "답변1"},
        {"question": "질문2", "answer": "답변2"}
      ]
      """
          .formatted(FAQ_COUNT);

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final SearchDocumentPort searchDocumentPort;
  private final ChatClient chatClient;

  public GenerateProductFaqSamplesUseCase(
      SearchDocumentPort searchDocumentPort, @Qualifier("reportChatClient") ChatClient chatClient) {
    this.searchDocumentPort = searchDocumentPort;
    this.chatClient = chatClient;
  }

  public GenerateProductFaqSamplesResult execute(Long productId) {
    List<Document> docs = searchDocumentPort.search(productId, SEED_QUERY, TOP_K);

    if (docs.isEmpty()) {
      log.warn("사전 Q&A 생성 실패 - 임베딩된 문서 없음. productId={}", productId);
      return new GenerateProductFaqSamplesResult(Collections.emptyList());
    }

    String context =
        docs.stream().map(Document::getText).collect(Collectors.joining("\n\n---\n\n"));

    String raw =
        chatClient
            .prompt()
            .system(SYSTEM_PROMPT.replace("{context}", context))
            .user(USER_PROMPT)
            .call()
            .content();

    if (!StringUtils.hasText(raw)) {
      return new GenerateProductFaqSamplesResult(Collections.emptyList());
    }

    try {
      String json = raw.trim();
      if (json.startsWith("```")) {
        json = json.replaceAll("(?s)^```[a-z]*\\n?", "").replaceAll("```$", "").trim();
      }
      List<FaqSampleItem> samples =
          MAPPER.readValue(json, new TypeReference<List<FaqSampleItem>>() {});

      log.info("사전 Q&A 생성 완료 - productId={}, count={}", productId, samples.size());
      return new GenerateProductFaqSamplesResult(samples);
    } catch (Exception e) {
      log.error("사전 Q&A JSON 파싱 실패 - productId={}: {}", productId, e.getMessage());
      return new GenerateProductFaqSamplesResult(Collections.emptyList());
    }
  }
}
