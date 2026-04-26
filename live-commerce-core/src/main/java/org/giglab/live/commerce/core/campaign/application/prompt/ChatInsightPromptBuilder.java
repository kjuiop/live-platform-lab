package org.giglab.live.commerce.core.campaign.application.prompt;

import org.giglab.live.commerce.core.campaign.application.dto.CampaignInsightResult;
import org.springframework.stereotype.Component;

@Component
public class ChatInsightPromptBuilder {

  public String build(CampaignInsightResult insight) {
    StringBuilder sb = new StringBuilder();
    sb.append("당신은 라이브 커머스 방송 분석 전문가입니다. 아래 방송 데이터를 분석해 판매자 인사이트 리포트를 작성해주세요.\n\n");

    sb.append("## 작성 원칙\n");
    sb.append("- 아래 제공된 채팅 샘플과 수치 데이터를 분석의 출발점으로 삼으세요.\n");
    sb.append("- 데이터에서 확인된 패턴을 해석하고, 그 의미와 제안으로 발전시키는 것은 권장합니다.\n");
    sb.append("- 단, 채팅에 없는 키워드를 언급하거나, 제공되지 않은 수치·사건을 사실처럼 서술하지 마세요.\n");
    sb.append("- 샘플이 없는 항목은 수치 데이터만으로 분석하고, 그마저 없으면 해당 없음으로 표기하세요.\n\n");

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

    if (!insight.contextMessages().isEmpty()) {
      sb.append("## 맥락 보완 채팅 샘플 (감성 중립 일반 채팅)\n");
      sb.append("※ 긍정/부정 샘플이 부족해 추가 수집한 채팅입니다. 분류 없이 맥락 파악용으로만 참고하세요.\n");
      insight.contextMessages().forEach(m -> sb.append("- ").append(m).append("\n"));
      sb.append("\n");
    }

    if (!insight.unansweredQuestions().isEmpty()) {
      sb.append("## 미답변 질문 목록\n");
      insight.unansweredQuestions().forEach(q -> sb.append("- ").append(q).append("\n"));
      sb.append("\n");
    }

    sb.append("## 리포트 형식 (다음 항목을 포함해 한국어로 작성)\n");
    sb.append("1. 핵심 관심사 Top 5 (채팅 샘플에 등장한 키워드 중심으로, 각 키워드가 어떤 맥락에서 언급됐는지 설명)\n");
    sb.append("2. 구매 의도 신호 분석 (채팅에서 읽히는 구매 관심도와 그 근거)\n");
    sb.append("3. 부정 반응 분석 (부정 채팅의 패턴을 요약하고, 판매자 관점에서 어떤 개선이 필요한지 제안)\n");
    sb.append("4. 미답변 질문 (다음 방송 FAQ 준비용, 위 목록 기반으로 우선순위 제안)\n");
    sb.append("5. 전체 방송 총평 (수치와 채팅 분위기를 종합해 1~2문장)\n");
    sb.append("6. 호스트 액션 제안 (이번 방송 데이터에서 발견된 패턴을 근거로 다음 방송 개선점 제안)\n");

    return sb.toString();
  }
}
