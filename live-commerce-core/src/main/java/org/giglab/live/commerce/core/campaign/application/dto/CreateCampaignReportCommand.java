package org.giglab.live.commerce.core.campaign.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CreateCampaignReportCommand(
    String chatRoomId,
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

  public CreateCampaignReportCommand {
    unansweredQuestions = unansweredQuestions != null ? unansweredQuestions : List.of();
  }
}
