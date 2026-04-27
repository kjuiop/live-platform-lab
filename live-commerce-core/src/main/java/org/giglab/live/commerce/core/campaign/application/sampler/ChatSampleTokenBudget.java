package org.giglab.live.commerce.core.campaign.application.sampler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatSampleTokenBudget {

  // 채팅 샘플에 할당된 토큰 예산. 프롬프트 전체(지시문 ~500 + 지표 ~50 + 채팅 1,800 + 질문 ~200 = ~2,550)에서
  // 채팅 비중을 고정해 비용을 통제한다. 긍정+부정 20건씩 기준 실사용량 ~1,560토큰으로 예산 안에 수납된다.
  private static final int CHAT_TOKEN_BUDGET = 1800;

  // 레이블(POSITIVE/NEGATIVE/NEUTRAL)별 최대 추출 건수. 긍정·부정 균형을 위해 동일한 상한을 적용한다.
  private static final int LABEL_SAMPLE_SIZE = 20;

  /** 레이블 맵에서 특정 레이블에 해당하는 메시지를 최대 20건 추출한다. */
  public static List<String> sampleByLabel(Map<String, String> labels, String targetLabel) {
    return labels.entrySet().stream()
        .filter(e -> targetLabel.equals(e.getValue()))
        .map(Map.Entry::getKey)
        .limit(LABEL_SAMPLE_SIZE)
        .toList();
  }

  /** 긍정+부정 샘플의 합산 토큰이 예산 절반 미달이면 NEUTRAL 보완이 필요하다고 판단한다. */
  public static boolean needsFallback(List<String> positive, List<String> negative) {
    int total =
        positive.stream().mapToInt(ChatSampleTokenBudget::estimateTokens).sum()
            + negative.stream().mapToInt(ChatSampleTokenBudget::estimateTokens).sum();
    return total < (CHAT_TOKEN_BUDGET / 2);
  }

  /** 토큰 예산 내에서 앞에서부터 메시지를 채운다. */
  public static List<String> applyBudget(List<String> messages) {
    int used = 0;
    List<String> result = new ArrayList<>();
    for (String msg : messages) {
      int est = estimateTokens(msg);
      if (used + est > CHAT_TOKEN_BUDGET) {
        break;
      }
      result.add(msg);
      used += est;
    }
    return result;
  }

  /** gpt-4o-mini (o200k_base) 기준 토큰 추정. 한국어 한 글자 ≈ 1토큰이며, 공백·구두점·혼합 영어를 고려해 × 1.3 적용. */
  public static int estimateTokens(String text) {
    return (int) (text.length() * 1.3);
  }
}
