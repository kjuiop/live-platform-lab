package org.giglab.live.commerce.core.campaign.application;

import org.giglab.live.commerce.core.campaign.application.dto.CampaignInsightResult;
import org.springframework.stereotype.Component;

@Component
public class ChatInsightPromptBuilder {

  public String build(CampaignInsightResult insight) {
    StringBuilder sb = new StringBuilder();
    sb.append("당신은 라이브 커머스 방송 분석 전문가입니다. 아래 방송 데이터를 분석해 판매자 인사이트 리포트를 작성해주세요.\n\n");

    sb.append("## 방송 지표\n");
    sb.append(String.format("- 총 시청자: %d명%n", insight.totalViewers()));
    sb.append(String.format("- 최고 동시 시청자: %d명%n", insight.peakConcurrent()));
    sb.append(String.format("- 평균 시청 시간: %d초%n", insight.avgDurationSeconds()));
    sb.append(
        String.format(
            "- 총 채팅: %d건 / FAQ 질문: %d건 / AI 답변: %d건%n%n",
            insight.totalMessages(), insight.totalQuestions(), insight.aiAnswerCount()));

    if (!insight.positiveMessages().isEmpty()) {
      sb.append("## 긍정 반응 채팅 샘플\n");
      insight.positiveMessages().forEach(m -> sb.append("- ").append(m).append("\n"));
      sb.append("\n");
    }

    if (!insight.negativeMessages().isEmpty()) {
      sb.append("## 부정 반응 채팅 샘플\n");
      insight.negativeMessages().forEach(m -> sb.append("- ").append(m).append("\n"));
      sb.append("\n");
    }

    if (!insight.unansweredQuestions().isEmpty()) {
      sb.append("## 미답변 질문 목록\n");
      insight.unansweredQuestions().forEach(q -> sb.append("- ").append(q).append("\n"));
      sb.append("\n");
    }

    sb.append("## 리포트 형식 (다음 항목을 포함해 한국어로 작성)\n");
    sb.append("1. 핵심 관심사 Top 5 (키워드 + 언급 맥락)\n");
    sb.append("2. 구매 의도 신호 분석\n");
    sb.append("3. 부정 반응 원인 추정\n");
    sb.append("4. 미답변 질문 (다음 방송 FAQ 준비용)\n");
    sb.append("5. 전체 방송 총평 (1~2문장)\n");
    sb.append("6. 호스트 액션 제안\n");

    return sb.toString();
  }
}
