package org.giglab.live.commerce.core.campaign.application.prompt;

import org.giglab.live.commerce.core.campaign.application.dto.CampaignInsightResult;
import org.springframework.stereotype.Component;

@Component
public class ChatInsightPromptBuilder {

  public String build(CampaignInsightResult insight) {
    StringBuilder sb = new StringBuilder();
    sb.append("당신은 라이브 커머스 방송 분석 전문가입니다. 아래 방송 데이터를 분석해 판매자 인사이트 리포트를 작성해주세요.\n\n");

    sb.append("## 작성 원칙\n");
    sb.append("- 아래 제공된 채팅 샘플과 수치 데이터만을 근거로 분석하세요.\n");
    sb.append("- 데이터에서 확인된 패턴을 해석하고, 그 의미와 제안으로 발전시키는 것은 권장합니다.\n");
    sb.append("- 샘플이 없는 항목은 수치 데이터만으로 분석하고, 그마저 없으면 '해당 없음'으로 표기하세요.\n\n");

    sb.append("## 절대 금지 사항 (반드시 준수)\n");
    sb.append("- 아래 채팅 샘플·FAQ 목록에 등장하지 않은 키워드, 주제, 상품 정보는 절대 언급하지 마세요.\n");
    sb.append("- AI 학습 지식이나 상품에 대한 일반 상식으로 내용을 보완하거나 추론하지 마세요.\n");
    sb.append("- '배송', '수령', '반품', '교환' 등 채팅에 실제로 등장하지 않은 주제는 언급 금지입니다.\n");
    sb.append("- 핵심 관심사가 5개 미만이면 실제 언급된 수만큼만 작성하고, 채우기 위해 추가하지 마세요.\n");
    sb.append("- 수치나 사건을 사실처럼 서술할 때는 반드시 위 방송 지표 또는 채팅 샘플에 근거해야 합니다.\n\n");

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
    sb.append("1. 핵심 관심사 (채팅 샘플에 실제 등장한 키워드만 나열, 최대 5개 / 없으면 '해당 없음')\n");
    sb.append("   - 각 키워드가 채팅에서 어떤 맥락으로 언급됐는지 샘플 기반으로 설명\n");
    sb.append("2. 구매 의도 신호 분석 (채팅 샘플에서 읽히는 구매 관심도와 해당 채팅 근거 명시)\n");
    sb.append("3. 부정 반응 분석 (부정 채팅 샘플이 있을 경우에만 작성, 없으면 '해당 없음')\n");
    sb.append("4. 미답변 질문 (위 미답변 질문 목록 기반으로만 작성, 목록이 없으면 '해당 없음')\n");
    sb.append("5. 전체 방송 총평 (제공된 수치와 채팅 샘플 분위기만을 근거로 1~2문장)\n");
    sb.append("6. 호스트 액션 제안 (채팅 샘플과 수치에서 발견된 패턴만을 근거로 작성)\n");

    return sb.toString();
  }
}
