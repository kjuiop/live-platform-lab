package org.giglab.live.commerce.core.campaign.application.dto;

import java.util.List;

public record CampaignInsightResult(
    int totalViewers,
    int peakConcurrent,
    long avgDurationSeconds,
    int totalMessages,
    int totalQuestions,
    int aiAnswerCount,
    List<String> positiveMessages,
    List<String> negativeMessages,
    List<String> unansweredQuestions) {}
