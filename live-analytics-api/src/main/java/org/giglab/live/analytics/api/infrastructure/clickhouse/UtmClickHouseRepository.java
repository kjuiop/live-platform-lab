package org.giglab.live.analytics.api.infrastructure.clickhouse;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.analytics.api.application.dto.GetUtmResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UtmClickHouseRepository {

  private final JdbcTemplate jdbcTemplate;

  public List<GetUtmResult> getUtmByRoomId(
      String roomId, LocalDateTime startAt, LocalDateTime endAt) {
    String sql =
        """
        SELECT
            utm_source,
            COUNT(DISTINCT session_id)                          AS visitors,
            countIf(event_type = 'product.purchase')            AS purchases,
            round(countIf(event_type = 'product.purchase') * 100.0
                / nullIf(COUNT(DISTINCT session_id), 0), 2)     AS cvr_pct
        FROM analytics.events
        WHERE room_id = ?
          AND utm_source IS NOT NULL
          AND occurred_at >= ?
          AND occurred_at < ?
        GROUP BY utm_source
        ORDER BY purchases DESC
        """;

    return jdbcTemplate.query(
        sql,
        (rs, rowNum) ->
            new GetUtmResult(
                rs.getString("utm_source"),
                rs.getLong("visitors"),
                rs.getLong("purchases"),
                rs.getDouble("cvr_pct")),
        roomId,
        startAt,
        endAt);
  }
}
