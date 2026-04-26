package org.giglab.live.commerce.core.campaign.application.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record CampaignInsightResult(
    int totalViewers,
    int peakConcurrent,
    long avgDurationSeconds,
    int totalMessages,
    int totalQuestions,
    int aiAnswerCount,
    List<String> rawMessages,
    List<String> positiveMessages,
    List<String> negativeMessages,
    List<String> contextMessages,
    List<String> unansweredQuestions) {

  /** live-chat-server REST 응답 역직렬화용 */
  @JsonCreator
  public static CampaignInsightResult fromRaw(
      @JsonProperty("totalViewers") int totalViewers,
      @JsonProperty("peakConcurrent") int peakConcurrent,
      @JsonProperty("avgDurationSeconds") long avgDurationSeconds,
      @JsonProperty("totalMessages") int totalMessages,
      @JsonProperty("totalQuestions") int totalQuestions,
      @JsonProperty("aiAnswerCount") int aiAnswerCount,
      @JsonProperty("rawMessages") List<String> rawMessages,
      @JsonProperty("unansweredQuestions") List<String> unansweredQuestions) {
    return new CampaignInsightResult(
        totalViewers,
        peakConcurrent,
        avgDurationSeconds,
        totalMessages,
        totalQuestions,
        aiAnswerCount,
        rawMessages != null ? rawMessages : List.of(),
        List.of(),
        List.of(),
        List.of(),
        unansweredQuestions != null ? unansweredQuestions : List.of());
  }

  /** 분류 완료 후 샘플 결과를 채운 인스턴스 반환 */
  public CampaignInsightResult withClassified(
      List<String> positiveMessages, List<String> negativeMessages, List<String> contextMessages) {
    return new CampaignInsightResult(
        this.totalViewers,
        this.peakConcurrent,
        this.avgDurationSeconds,
        this.totalMessages,
        this.totalQuestions,
        this.aiAnswerCount,
        this.rawMessages,
        positiveMessages,
        negativeMessages,
        contextMessages,
        this.unansweredQuestions);
  }
}
