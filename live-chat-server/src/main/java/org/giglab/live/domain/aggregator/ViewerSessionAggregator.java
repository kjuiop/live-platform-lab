package org.giglab.live.domain.aggregator;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.giglab.live.application.dto.stats.ViewerStats;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ViewerSessionAggregator {

  private final MongoTemplate mongoTemplate;

  public ViewerStats aggregate(String roomId) {
    // 중복 sessionId 제거를 위해 sessionId별 group 후 카운트
    Aggregation distinctViewerAgg =
        Aggregation.newAggregation(
            Aggregation.match(Criteria.where("roomId").is(roomId)),
            Aggregation.group("sessionId").avg("durationSeconds").as("avgDuration"),
            Aggregation.group()
                .count()
                .as("totalViewers")
                .avg("avgDuration")
                .as("avgDurationSeconds"));

    AggregationResults<Map> viewerResult =
        mongoTemplate.aggregate(distinctViewerAgg, "viewer_sessions", Map.class);

    // 최고 동시 시청자
    Aggregation peakAgg =
        Aggregation.newAggregation(
            Aggregation.match(Criteria.where("roomId").is(roomId)),
            Aggregation.group().max("viewerCount").as("peakConcurrent"));

    AggregationResults<Map> peakResult =
        mongoTemplate.aggregate(peakAgg, "peak_viewer_snapshots", Map.class);

    Map viewerMap = viewerResult.getUniqueMappedResult();
    Map peakMap = peakResult.getUniqueMappedResult();

    int totalViewers = viewerMap != null ? ((Number) viewerMap.get("totalViewers")).intValue() : 0;
    long avgDurationSeconds =
        viewerMap != null
            ? Math.round(((Number) viewerMap.get("avgDurationSeconds")).doubleValue())
            : 0L;
    int peakConcurrent = peakMap != null ? ((Number) peakMap.get("peakConcurrent")).intValue() : 0;

    return new ViewerStats(totalViewers, peakConcurrent, avgDurationSeconds);
  }
}
