package org.giglab.live.analytics.api.infrastructure.clickhouse;

import lombok.RequiredArgsConstructor;
import org.giglab.live.analytics.api.application.dto.GetViewerResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ViewerClickHouseRepository {

  private final JdbcTemplate jdbcTemplate;

  public GetViewerResult getViewerByRoomId(String roomId) {
    String sql =
        """
        SELECT COUNT(DISTINCT session_id) AS total_viewers
        FROM analytics.events
        WHERE room_id = ?
          AND event_type = 'stream.join'
        """;

    Long total = jdbcTemplate.queryForObject(sql, Long.class, roomId);
    return new GetViewerResult(total != null ? total : 0L);
  }
}
