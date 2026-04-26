package org.giglab.live.commerce.core.campaign.application.dto;

import java.time.LocalDateTime;
import org.giglab.live.commerce.core.campaign.domain.entity.CampaignReport;

public record GetCampaignReportResult(
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
    LocalDateTime endedAt) {

  public static GetCampaignReportResult from(CampaignReport report) {
    return new GetCampaignReportResult(
        report.getId(),
        report.getCampaignId(),
        report.getRoomId(),
        report.getTotalViewers(),
        report.getPeakConcurrent(),
        report.getAvgDurationSeconds(),
        report.getTotalMessages(),
        report.getTotalQuestions(),
        report.getAiAnswerCount(),
        report.getAiReportText(),
        report.getStartedAt(),
        report.getEndedAt());
  }
}
