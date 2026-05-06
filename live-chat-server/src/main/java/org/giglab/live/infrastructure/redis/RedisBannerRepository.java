package org.giglab.live.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.application.dto.action.ActiveBanner;
import org.giglab.live.infrastructure.redis.exception.RedisException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisBannerRepository {

  private static final String BANNER_KEY = "LIVE:ROOM:%s:BANNER";
  private static final Duration BANNER_TTL = Duration.ofDays(7);

  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper objectMapper;

  public void save(String roomId, ActiveBanner banner) {
    String key = bannerKey(roomId);
    try {
      redisTemplate.opsForValue().set(key, banner, BANNER_TTL);
    } catch (Exception e) {
      log.error("배너 상태 저장 실패 - roomId={}", roomId, e);
      throw new RedisException("배너 상태 저장 실패 - roomId: " + roomId);
    }
  }

  public void delete(String roomId) {
    String key = bannerKey(roomId);
    try {
      redisTemplate.delete(key);
    } catch (Exception e) {
      log.error("배너 상태 삭제 실패 - roomId={}", roomId, e);
      throw new RedisException("배너 상태 삭제 실패 - roomId: " + roomId);
    }
  }

  public Optional<ActiveBanner> find(String roomId) {
    String key = bannerKey(roomId);
    try {
      Object value = redisTemplate.opsForValue().get(key);
      if (value == null) {
        return Optional.empty();
      }
      return Optional.of(objectMapper.convertValue(value, ActiveBanner.class));
    } catch (Exception e) {
      log.error("배너 상태 조회 실패 - roomId={}", roomId, e);
      return Optional.empty();
    }
  }

  private String bannerKey(String roomId) {
    return String.format(BANNER_KEY, roomId);
  }
}
