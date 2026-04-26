package org.giglab.live.commerce.api.dto.campaign;

import java.time.LocalDateTime;
import java.util.List;
import org.giglab.live.commerce.core.campaign.application.dto.GetCampaignReportResult;

public record GetCampaignReportResponse(
    Long id,
    Long campaignId,
    String roomId,
    int totalViewers,
    int peakConcurrent,
    long avgDurationSeconds,
    int totalMessages,
    int totalQuestions,
    int aiAnswerCount,
    String aiReportText,
    LocalDateTime startedAt,
    LocalDateTime endedAt,
    List<String> unansweredQuestions) {

  public static GetCampaignReportResponse from(GetCampaignReportResult result) {
    return new GetCampaignReportResponse(
        result.id(),
        result.campaignId(),
        result.roomId(),
        result.totalViewers(),
        result.peakConcurrent(),
        result.avgDurationSeconds(),
        result.totalMessages(),
        result.totalQuestions(),
        result.aiAnswerCount(),
        result.aiReportText(),
        result.startedAt(),
        result.endedAt(),
        result.unansweredQuestions());
  }
}
