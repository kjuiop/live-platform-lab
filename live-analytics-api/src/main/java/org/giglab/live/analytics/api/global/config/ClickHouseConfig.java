package org.giglab.live.analytics.api.global.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class ClickHouseConfig {

  @Bean
  public JdbcTemplate clickHouseJdbcTemplate(DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }
}
