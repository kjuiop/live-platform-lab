package org.giglab.live.infrastructure.redis;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ViewerRedisRepository {

  private static final String VIEWERS_KEY = "LIVE:ROOM:%s:VIEWERS";
  private static final String SESSION_KEY = "LIVE:VIEWER:%s";
  private static final String FIELD_JOIN_AT = "joinAt";
  private static final String FIELD_ROOM_ID = "roomId";
  private static final String FIELD_USER_ID = "userId";
  private static final Duration VIEWER_TTL = Duration.ofHours(12);

  private final RedisTemplate<String, Object> redisTemplate;

  public void addViewer(String roomId, String sessionId) {
    String sessionKey = sessionKey(sessionId);
    Map<String, String> fields =
        Map.of(FIELD_JOIN_AT, String.valueOf(Instant.now().toEpochMilli()), FIELD_ROOM_ID, roomId);

    redisTemplate.execute(
        // 하나의 커넥션에서 명령을 수행
        new SessionCallback<List<Object>>() {
          @Override
          @SuppressWarnings("unchecked")
          public <K, V> List<Object> execute(RedisOperations<K, V> ops) throws DataAccessException {
            ops.multi();
            // session Hash 저장
            // session id 별 joinAt, roomId, userId 정보 저장 (userId는 CHAT_JOIN 시점에 업데이트)
            // LEAVE 시점에 joinAt과 roomId 조회해서 시청기록과 roomId 저장
            ops.opsForHash().putAll((K) sessionKey, fields);
            ops.expire((K) sessionKey, VIEWER_TTL);
            // viewer key 에서 sessionId 추가
            // SCARD 한 번으로 동시 시청자 수 조회하도록 사용
            ops.opsForSet().add((K) viewersKey(roomId), (V) sessionId);
            ops.expire((K) viewersKey(roomId), VIEWER_TTL);
            return ops.exec();
          }
        });
  }

  public void saveUserId(String sessionId, String userId) {
    String key = sessionKey(sessionId);
    Boolean exists = redisTemplate.hasKey(key);
    if (!Boolean.TRUE.equals(exists)) {
      log.warn("saveUserId 스킵 - 세션 Hash 없음: sessionId={}", sessionId);
      return;
    }
    redisTemplate.opsForHash().put(key, FIELD_USER_ID, userId);
    redisTemplate.expire(key, VIEWER_TTL);
  }

  public Optional<ViewerContext> getAndRemoveViewer(String sessionId) {
    Map<Object, Object> entries = redisTemplate.opsForHash().entries(sessionKey(sessionId));

    String roomId = (String) entries.get(FIELD_ROOM_ID);
    String joinAtStr = (String) entries.get(FIELD_JOIN_AT);

    if (roomId == null || joinAtStr == null) {
      return Optional.empty();
    }

    final String userId = (String) entries.get(FIELD_USER_ID);
    final Instant joinAt = Instant.ofEpochMilli(Long.parseLong(joinAtStr));

    String sessionHashKey = sessionKey(sessionId);
    String viewersSetKey = viewersKey(roomId);
    redisTemplate.execute(
        new SessionCallback<List<Object>>() {
          @Override
          @SuppressWarnings("unchecked")
          public <K, V> List<Object> execute(RedisOperations<K, V> ops) throws DataAccessException {
            ops.multi();
            ops.opsForSet().remove((K) viewersSetKey, (V) sessionId);
            ops.delete((K) sessionHashKey);
            return ops.exec();
          }
        });

    Long remaining = redisTemplate.opsForSet().size(viewersSetKey);
    if (remaining != null && remaining == 0) {
      redisTemplate.delete(viewersSetKey);
      log.debug("빈 VIEWERS Set 삭제 - roomId={}", roomId);
    }

    return Optional.of(new ViewerContext(sessionId, roomId, userId, joinAt));
  }

  public long getViewerCount(String roomId) {
    Long count = redisTemplate.opsForSet().size(viewersKey(roomId));
    return count != null ? count : 0L;
  }

  public Set<String> getActiveRoomIds() {
    ScanOptions options = ScanOptions.scanOptions().match("LIVE:ROOM:*:VIEWERS").count(100).build();

    Set<String> roomIds = new HashSet<>();
    redisTemplate.execute(
        (RedisCallback<Void>)
            connection -> {
              try (Cursor<byte[]> cursor = connection.keyCommands().scan(options)) {
                while (cursor.hasNext()) {
                  String key = new String(cursor.next(), StandardCharsets.UTF_8);
                  // "LIVE:ROOM:{roomId}:VIEWERS" → roomId
                  String[] parts = key.split(":");
                  if (parts.length >= 3) {
                    roomIds.add(parts[2]);
                  } else {
                    log.warn("예상치 못한 Redis 키 포맷 - key={}", key);
                  }
                }
              } catch (Exception e) {
                log.error("활성 방 목록 SCAN 실패: {}", e.getMessage(), e);
              }
              return null;
            });
    return roomIds;
  }

  private String viewersKey(String roomId) {
    return String.format(VIEWERS_KEY, roomId);
  }

  private String sessionKey(String sessionId) {
    return String.format(SESSION_KEY, sessionId);
  }

  public record ViewerContext(String sessionId, String roomId, String userId, Instant joinAt) {}
}
