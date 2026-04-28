package org.giglab.live.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.application.port.persistence.RoomPort;
import org.giglab.live.domain.model.Room;
import org.giglab.live.infrastructure.redis.exception.RedisOperationException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class RedisRoomRepository implements RoomPort {

  private static final String ROOM_KEY_PREFIX = "LIVE:ROOM";
  private static final String ROOM_INDEX_KEY = "LIVE:ROOM:INDEX";
  private static final Duration ROOM_TTL = Duration.ofDays(7);

  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper objectMapper;

  public RedisRoomRepository(
      RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
    this.redisTemplate = redisTemplate;
    this.objectMapper = objectMapper;
  }

  @Override
  public Room save(Room room) {
    String roomKey = String.format("%s:%s", ROOM_KEY_PREFIX, room.getRoomId());

    try {

      Instant createdAt = room.getCreatedAt();
      long score = createdAt.toEpochMilli();

      List<Object> results =
          redisTemplate.execute(
              new SessionCallback<List<Object>>() {
                @Override
                @SuppressWarnings("unchecked")
                public <K, V> List<Object> execute(RedisOperations<K, V> operations)
                    throws DataAccessException {
                  operations.multi();

                  operations.opsForValue().set((K) roomKey, (V) room, ROOM_TTL);
                  operations.opsForZSet().add((K) ROOM_INDEX_KEY, (V) room.getRoomId(), score);
                  return operations.exec();
                }
              });

      if (results == null || results.isEmpty()) {
        throw new RedisOperationException("SAVE", roomKey, "Transaction failed", null);
      }

      return room;
    } catch (Exception e) {
      log.error(
          "Failed to save room to Redis: roomId={}, key={}, error={}",
          room.getRoomId(),
          roomKey,
          e.getMessage(),
          e);
      throw new RedisOperationException("SAVE", roomKey, e.getMessage(), e);
    }
  }

  @Override
  public List<String> findLatestRoomIds(int limit) {

    try {
      ZSetOperations<String, Object> zsetOps = redisTemplate.opsForZSet();
      Set<Object> roomIds = zsetOps.reverseRange(ROOM_INDEX_KEY, 0, limit - 1);

      if (roomIds == null || roomIds.isEmpty()) {
        return Collections.emptyList();
      }

      return roomIds.stream().map(Object::toString).toList();

    } catch (Exception e) {
      log.error("Failed to get latest rooms: limit={}, error={}", limit, e.getMessage(), e);
      throw new RedisOperationException("GET_LATEST_ROOMS", ROOM_INDEX_KEY, e.getMessage(), e);
    }
  }

  @Override
  public Stream<Room> getRoomsByIds(List<String> roomIds) {
    if (roomIds.isEmpty()) {
      return Stream.empty();
    }

    try {
      List<String> keys =
          roomIds.stream().map(roomId -> String.format("%s:%s", ROOM_KEY_PREFIX, roomId)).toList();

      List<Object> rooms = redisTemplate.opsForValue().multiGet(keys);
      if (rooms == null || rooms.isEmpty()) {
        return Stream.empty();
      }

      List<String> expiredRoomIds =
          IntStream.range(0, rooms.size())
              .filter(i -> rooms.get(i) == null)
              .mapToObj(roomIds::get)
              .toList();

      if (!expiredRoomIds.isEmpty()) {
        removeIndexAsync(expiredRoomIds);
      }

      return rooms.stream().filter(Objects::nonNull).map(this::convertToRoom);
    } catch (Exception e) {
      log.error("Failed to get rooms: roomId={}, error={}", roomIds, e.getMessage(), e);
      throw new RedisOperationException("FIND_BY_IDS", ROOM_KEY_PREFIX, e.getMessage(), e);
    }
  }

  @Deprecated
  public List<Room> getRoomsByIdsAsList(List<String> roomIds) {
    if (roomIds.isEmpty()) {
      return Collections.emptyList();
    }

    try {
      // 1. key 순회
      List<String> keys =
          roomIds.stream().map(roomId -> String.format("%s:%s", ROOM_KEY_PREFIX, roomId)).toList();

      List<Object> rooms = redisTemplate.opsForValue().multiGet(keys);

      // 2. Room 으로 변환
      // 3. service 에서 다시 GetRoomResponse 로 변환
      return rooms.stream()
          .filter(Objects::nonNull)
          .map(this::convertToRoom)
          .collect(Collectors.toList());
    } catch (Exception e) {
      log.error("Failed to get rooms: roomId={}, error={}", roomIds, e.getMessage(), e);
      throw new RedisOperationException("FIND_BY_IDS", ROOM_KEY_PREFIX, e.getMessage(), e);
    }
  }

  @Override
  public void deleteById(String roomId) {
    String roomKey = String.format("%s:%s", ROOM_KEY_PREFIX, roomId);

    try {
      List<Object> results =
          redisTemplate.execute(
              new SessionCallback<List<Object>>() {
                @Override
                @SuppressWarnings("unchecked")
                public <K, V> List<Object> execute(RedisOperations<K, V> operations)
                    throws DataAccessException {
                  operations.multi();
                  operations.delete((K) roomKey);
                  operations.opsForZSet().remove((K) ROOM_INDEX_KEY, (V) roomId);
                  return operations.exec();
                }
              });

      if (results == null) {
        throw new RedisOperationException("DELETE", roomKey, "Transaction returned null", null);
      }
      long deleted = results.getFirst() instanceof Long l ? l : 0L;
      if (deleted > 0) {
        log.info("채팅방 삭제 완료 - roomId={}, deleted={}", roomId, deleted);
      } else {
        log.debug("채팅방 삭제 요청 - 이미 존재하지 않는 roomId={}", roomId);
      }

    } catch (RedisOperationException e) {
      throw e;
    } catch (Exception e) {
      log.error("Failed to delete room: roomId={}, error={}", roomId, e.getMessage(), e);
      throw new RedisOperationException("DELETE", roomKey, e.getMessage(), e);
    }
  }

  private Room convertToRoom(Object obj) {
    if (obj == null) {
      return null;
    }
    if (obj instanceof Room) {
      return (Room) obj;
    }

    return objectMapper.convertValue(obj, Room.class);
  }

  private void removeIndexAsync(List<String> expiredRoomIds) {
    CompletableFuture.runAsync(
        () -> {
          try {
            ZSetOperations<String, Object> zsetOps = redisTemplate.opsForZSet();
            zsetOps.remove(ROOM_INDEX_KEY, expiredRoomIds.toArray());
          } catch (Exception e) {
            log.error("Failed to remove expired room IDs: error={}", e.getMessage(), e);
          }
        });
  }
}
