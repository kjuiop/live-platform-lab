package org.giglab.live.application.dto.stats;

import java.util.List;

public record ChatStats(
    int totalMessages, int totalQuestions, int aiAnswerCount, List<String> unansweredQuestions) {}
