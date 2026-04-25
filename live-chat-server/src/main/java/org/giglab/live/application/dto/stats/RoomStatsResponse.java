package org.giglab.live.application.dto.stats;

public record RoomStatsResponse(
    int totalViewers,
    int peakConcurrent,
    long avgDurationSeconds,
    int totalMessages,
    int totalQuestions,
    int aiAnswerCount) {
  public static RoomStatsResponse of(ChatStats chatStats, ViewerStats viewerStats) {
    return new RoomStatsResponse(
        viewerStats.totalViewers(),
        viewerStats.peakConcurrent(),
        viewerStats.avgDurationSeconds(),
        chatStats.totalMessages(),
        chatStats.totalQuestions(),
        chatStats.aiAnswerCount());
  }
}
