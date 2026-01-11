package org.giglab.live.infrastructure.redis.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author : JAKE
 * @date : 26. 1. 11.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisHealthCheck {

  private final RedisTemplate<String, Object> redisTemplate;

  @EventListener(ApplicationReadyEvent.class)
  public void checkRedisConnection() {
    try {
      redisTemplate.opsForValue().set("health:check", "ok");
      String value = (String) redisTemplate.opsForValue().get("health:check");
      redisTemplate.delete("health:check");

      if (!"ok".equals(value)) {
        throw new RuntimeException("Redis connection validation failed");
      }

      log.info("Redis connection successful");
    } catch (Exception e) {
      log.error("Redis connection failed: {}", e.getMessage());
      throw new RuntimeException("failed to connect to Redis", e);
    }
  }
}
