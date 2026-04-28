package org.giglab.live.commerce.api.dto.campaign;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateCampaignReportRequest(
    @NotBlank String chatRoomId,
    int totalViewers,
    int peakConcurrent,
    long avgDurationSeconds,
    int totalMessages,
    int totalQuestions,
    int aiAnswerCount,
    String aiReportText,
    @NotNull LocalDateTime startedAt,
    @NotNull LocalDateTime endedAt) {}
