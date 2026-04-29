package org.giglab.live.commerce.core.campaign.infrastructure.ai;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatSentimentClassifier {

  private static final Set<String> VALID_LABELS = Set.of("POSITIVE", "NEGATIVE", "NEUTRAL");

  private final ChatClient chatClient;

  public ChatSentimentClassifier(@Qualifier("classifierChatClient") ChatClient chatClient) {
    this.chatClient = chatClient;
  }

  /**
   * 메시지 목록을 LLM으로 배치 분류한다.
   *
   * @return 메시지 → 레이블(POSITIVE/NEGATIVE/NEUTRAL) 맵. 파싱 실패 시 NEUTRAL로 fallback.
   */
  public Map<String, String> classify(List<String> messages) {
    if (messages.isEmpty()) {
      return Map.of();
    }

    // 파싱 실패 대비: 먼저 전체를 NEUTRAL로 초기화
    Map<String, String> result = new LinkedHashMap<>();
    for (String msg : messages) {
      result.put(msg, "NEUTRAL");
    }

    try {
      String response = chatClient.prompt().user(buildPrompt(messages)).call().content();
      parseResponse(messages, response, result);
    } catch (Exception e) {
      log.warn("채팅 감성 분류 실패, 전체 NEUTRAL 처리 - size={}", messages.size(), e);
    }

    log.debug(
        "채팅 감성 분류 완료 - total={}, positive={}, negative={}, neutral={}",
        messages.size(),
        countByLabel(result, "POSITIVE"),
        countByLabel(result, "NEGATIVE"),
        countByLabel(result, "NEUTRAL"));

    return result;
  }

  private String buildPrompt(List<String> messages) {
    StringBuilder sb = new StringBuilder();
    sb.append("다음은 라이브 커머스 방송의 채팅 메시지입니다.\n");
    sb.append("각 메시지를 아래 기준으로 POSITIVE, NEGATIVE, NEUTRAL 중 하나로 분류하세요.\n\n");
    sb.append("[분류 기준]\n");
    sb.append("- POSITIVE: 구매 의향, 칭찬, 기대감, 재구매 언급\n");
    sb.append("- NEGATIVE: 가격 불만, 효과 의심, 배송 불만, 부정적 비교\n");
    sb.append("- NEUTRAL: 단순 질문, 정보 요청, 감정이 없는 반응\n\n");
    sb.append("출력 형식: 인덱스:레이블 (예: 0:POSITIVE) — 한 줄에 하나씩, 다른 설명 없이.\n\n");
    for (int i = 0; i < messages.size(); i++) {
      sb.append(i).append(". ").append(messages.get(i)).append("\n");
    }
    return sb.toString();
  }

  private void parseResponse(List<String> messages, String response, Map<String, String> result) {
    for (String line : response.split("\n")) {
      String[] parts = line.trim().split(":", 2);
      if (parts.length != 2) {
        continue;
      }
      try {
        int idx = Integer.parseInt(parts[0].trim());
        String label = parts[1].trim().toUpperCase();
        if (idx < messages.size() && VALID_LABELS.contains(label)) {
          result.put(messages.get(idx), label);
        }
      } catch (NumberFormatException e) {
        log.trace("분류 응답 라인 파싱 스킵 - line={}", line.trim());
      }
    }
  }

  private long countByLabel(Map<String, String> labels, String target) {
    return labels.values().stream().filter(target::equals).count();
  }
}
