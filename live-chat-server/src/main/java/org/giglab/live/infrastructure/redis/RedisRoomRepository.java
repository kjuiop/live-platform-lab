package org.giglab.live.infrastructure.redis;

import lombok.extern.slf4j.Slf4j;
import org.giglab.live.domain.model.Room;
import org.giglab.live.domain.repository.RoomRepository;
import org.giglab.live.infrastructure.redis.exception.RedisOperationException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author : JAKE
 * @date : 26. 1. 11.
 */
@Slf4j
@Repository
public class RedisRoomRepository implements RoomRepository {

  private static final String ROOM_KEY_PREFIX = "LIVE:ROOM";
  private static final String ROOM_INDEX_KEY = "LIVE:ROOM:INDEX";
  private static final Duration ROOM_TTL = Duration.ofDays(7);

  private final RedisTemplate<String, Object> redisTemplate;

  public RedisRoomRepository(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  @Override
  public Room save(Room room) {
    String roomKey = String.format("%s:%s", ROOM_KEY_PREFIX, room.getRoomId());

    try {

      LocalDateTime createdAt = room.getCreatedAt();
      long score = createdAt
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli();

      List<Object> results = redisTemplate.execute(new SessionCallback<List<Object>>() {
        @Override
        @SuppressWarnings("unchecked")
        public <K, V> List<Object> execute(RedisOperations<K, V> operations) throws DataAccessException {
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
      log.error("Failed to save room to Redis: roomId={}, key={}, error={}",
        room.getRoomId(), roomKey, e.getMessage(), e);
      throw new RedisOperationException("SAVE", roomKey, e.getMessage(), e);
    }
  }

  @Override
  public List<String> findLatestRoomIds(int limit) {

    try {
      ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
      Set<Object> roomIds = zSetOps.reverseRange(ROOM_INDEX_KEY, 0, limit - 1);

      if (roomIds == null || roomIds.isEmpty()) {
        return Collections.emptyList();
      }

      return roomIds.stream()
        .map(Object::toString)
        .toList();

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
      List<String> keys = roomIds.stream()
        .map(roomId -> String.format("%s:%s", ROOM_KEY_PREFIX, roomId))
        .toList();

      List<Object> rooms = redisTemplate.opsForValue().multiGet(keys);
      if (rooms == null || rooms.isEmpty()) {
        return Stream.empty();
      }

      return rooms.stream()
        .filter(Objects::nonNull)
        .map(obj -> (Room) obj);
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
      List<String> keys = roomIds.stream()
        .map(roomId -> String.format("%s:%s", ROOM_KEY_PREFIX, roomId))
        .toList();

      List<Object> rooms = redisTemplate.opsForValue().multiGet(keys);

      // 2. Room 으로 변환
      // 3. service 에서 다시 GetRoomResponse 로 변환
      return rooms.stream()
        .filter(Objects::nonNull)
        .map(obj -> (Room) obj)
        .collect(Collectors.toList());
    } catch (Exception e) {
      log.error("Failed to get rooms: roomId={}, error={}", roomIds, e.getMessage(), e);
      throw new RedisOperationException("FIND_BY_IDS", ROOM_KEY_PREFIX, e.getMessage(), e);
    }
  }

  @Override
  public Optional<Room> findById(String roomId) {
    String roomKey = String.format("%s:%s", ROOM_KEY_PREFIX, roomId);

    try {
      Room room = (Room) redisTemplate.opsForValue().get(roomKey);
      return Optional.ofNullable(room);
    } catch (Exception e) {
      log.error("Failed to find room by id: roomId={}, key={}, error={}",
        roomId, roomKey, e.getMessage(), e);
      throw new RedisOperationException("FIND_BY_ID", roomId, e.getMessage(), e);
    }
  }
}
