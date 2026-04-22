package org.giglab.live.commerce.core.product.application.usecase.ai;

import java.util.List;
import java.util.stream.Collectors;
import org.giglab.live.commerce.core.product.application.dto.ai.AskProductQuestionResult;
import org.giglab.live.commerce.core.product.application.port.ai.SearchDocumentPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

@Service
public class AskProductQuestionUseCase {

  private static final int TOP_K = 5;

  private static final String SYSTEM_PROMPT =
      """
      당신은 상품 전문 AI 어시스턴트입니다.
      아래 [상품 자료]를 근거로 질문에 정확하게 답변하세요.
      자료에 없는 내용은 "해당 정보를 찾을 수 없습니다"라고 답변하세요.

      [상품 자료]
      {context}
      """;

  private final SearchDocumentPort searchDocumentPort;
  private final ChatClient chatClient;

  public AskProductQuestionUseCase(SearchDocumentPort searchDocumentPort, ChatModel chatModel) {
    this.searchDocumentPort = searchDocumentPort;
    this.chatClient = ChatClient.create(chatModel);
  }

  public AskProductQuestionResult execute(Long productId, String question) {
    List<Document> docs = searchDocumentPort.search(productId, question, TOP_K);

    if (docs.isEmpty()) {
      return new AskProductQuestionResult("임베딩된 문서가 없어 답변을 제공할 수 없습니다. 먼저 PDF 문서를 임베딩해주세요.");
    }

    String context =
        docs.stream().map(Document::getText).collect(Collectors.joining("\n\n---\n\n"));

    String answer =
        chatClient
            .prompt()
            .system(SYSTEM_PROMPT.replace("{context}", context))
            .user(question)
            .call()
            .content();

    return new AskProductQuestionResult(answer);
  }
}
