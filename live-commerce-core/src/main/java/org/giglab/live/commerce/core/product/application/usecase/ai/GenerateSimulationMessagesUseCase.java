package org.giglab.live.commerce.core.product.application.usecase.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.commerce.core.product.application.dto.ai.SimulationMessagesResult;
import org.giglab.live.commerce.core.product.application.port.ai.SearchDocumentPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class GenerateSimulationMessagesUseCase {

  private static final int TOP_K = 5;
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private static final String SEED_QUERY = "상품 성분 사용법 주의사항 효과 부작용 보관방법";

  private static final String SYSTEM_PROMPT =
      """
      당신은 라이브 커머스 방송 시뮬레이션 전문가입니다.
      아래 [상품 자료]를 참고해 실제 방송에서 시청자들이 보낼 법한 채팅 반응과 FAQ 질문을 생성하세요.

      채팅 메시지는 시청자의 즉각적인 감정 반응으로, 긍정(구매 의사, 칭찬, 기대감) / 부정(가격 부담, 의구심, 불만) / 중립(단순 질문, 정보 요청) 톤을 골고루 섞어 자연스러운 한국어 구어체로 작성하세요.
      FAQ 질문은 상품 성분·사용법·주의사항 관련으로 작성하세요.

      [상품 자료]
      {context}
      """;

  private static final String USER_PROMPT =
      """
      위 상품에 대해 아래 JSON 형식으로만 반환하세요. 다른 설명 없이 JSON만 출력하세요.

      {
        "chatMessages": [
          "채팅 메시지 1",
          "채팅 메시지 2"
        ],
        "faqQuestions": [
          "FAQ 질문 1",
          "FAQ 질문 2"
        ]
      }

      chatMessages 200개(긍정 80개·부정 40개·중립 80개), faqQuestions 10개를 생성하세요.
      """;

  private final SearchDocumentPort searchDocumentPort;
  private final ChatClient chatClient;

  public GenerateSimulationMessagesUseCase(
      SearchDocumentPort searchDocumentPort, ChatModel chatModel) {
    this.searchDocumentPort = searchDocumentPort;
    this.chatClient = ChatClient.create(chatModel);
  }

  public SimulationMessagesResult execute(Long productId) {
    List<Document> docs = searchDocumentPort.search(productId, SEED_QUERY, TOP_K);

    if (docs.isEmpty()) {
      log.warn("[SimMsg] 임베딩 문서 없음 - productId={}", productId);
      return SimulationMessagesResult.empty();
    }

    String context =
        docs.stream().map(Document::getText).collect(Collectors.joining("\n\n---\n\n"));

    String raw =
        chatClient
            .prompt()
            .system(SYSTEM_PROMPT.replace("{context}", context))
            .user(USER_PROMPT)
            .options(OpenAiChatOptions.builder().temperature(0.8).build())
            .call()
            .content();

    if (!StringUtils.hasText(raw)) {
      return SimulationMessagesResult.empty();
    }

    try {
      String json = raw.trim();
      if (json.startsWith("```")) {
        json = json.replaceAll("(?s)^```[a-z]*\\n?", "").replaceAll("```$", "").trim();
      }
      JsonNode node = MAPPER.readTree(json);
      List<String> chatMessages =
          MAPPER.convertValue(node.get("chatMessages"), new TypeReference<List<String>>() {});
      List<String> faqQuestions =
          MAPPER.convertValue(node.get("faqQuestions"), new TypeReference<List<String>>() {});

      log.info(
          "[SimMsg] 생성 완료 - productId={}, chats={}, faqs={}",
          productId,
          chatMessages.size(),
          faqQuestions.size());
      return new SimulationMessagesResult(chatMessages, faqQuestions);
    } catch (Exception e) {
      log.error("[SimMsg] 파싱 실패 - productId={}: {}", productId, e.getMessage());
      return SimulationMessagesResult.empty();
    }
  }
}
