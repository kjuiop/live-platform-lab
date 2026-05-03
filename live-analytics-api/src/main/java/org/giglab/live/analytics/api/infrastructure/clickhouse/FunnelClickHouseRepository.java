package org.giglab.live.analytics.api.infrastructure.clickhouse;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.giglab.live.analytics.api.application.dto.GetFunnelResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FunnelClickHouseRepository {

  private final JdbcTemplate jdbcTemplate;

  public GetFunnelResult getFunnelByRoomId(
      String roomId, LocalDateTime startAt, LocalDateTime endAt) {

    String sql =
        """
        SELECT
          countIf(event_type = 'product.impression') AS impression,
          countIf(event_type = 'product.click') AS click,
          countIf(event_type = 'product.add_cart') AS add_cart,
          countIf(event_type = 'product.purchase') AS purchase,
          round(countIf(event_type = 'product.purchase') * 100.0
            / nullIf(countIf(event_type = 'product.impression'), 0), 2) AS cvr_pct
        FROM analytics.events
        WHERE room_id = ?
          AND event_type IN ('product.impression', 'product.click', 'product.add_cart', 'product.purchase')
          AND occurred_at >= ?
          AND occurred_at < ?
        """;

    return jdbcTemplate.queryForObject(
        sql,
        (rs, rowNum) ->
            new GetFunnelResult(
                rs.getLong("impression"),
                rs.getLong("click"),
                rs.getLong("add_cart"),
                rs.getLong("purchase"),
                rs.getDouble("cvr_pct")),
        roomId,
        startAt,
        endAt);
  }
}
