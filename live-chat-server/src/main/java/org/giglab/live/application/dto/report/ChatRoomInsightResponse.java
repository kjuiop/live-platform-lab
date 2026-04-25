package org.giglab.live.application.dto.report;

import java.util.List;
import org.giglab.live.application.dto.stats.ChatStats;
import org.giglab.live.application.dto.stats.ViewerStats;

public record ChatRoomInsightResponse(
    int totalViewers,
    int peakConcurrent,
    long avgDurationSeconds,
    int totalMessages,
    int totalQuestions,
    int aiAnswerCount,
    List<String> positiveMessages,
    List<String> negativeMessages,
    List<String> unansweredQuestions) {

  public static ChatRoomInsightResponse of(
      ViewerStats viewer,
      ChatStats chat,
      List<String> positiveMessages,
      List<String> negativeMessages) {
    return new ChatRoomInsightResponse(
        viewer.totalViewers(),
        viewer.peakConcurrent(),
        viewer.avgDurationSeconds(),
        chat.totalMessages(),
        chat.totalQuestions(),
        chat.aiAnswerCount(),
        positiveMessages,
        negativeMessages,
        chat.unansweredQuestions());
  }
}
