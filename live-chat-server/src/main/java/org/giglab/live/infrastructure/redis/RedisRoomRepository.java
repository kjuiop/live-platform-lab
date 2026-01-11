package org.giglab.live.infrastructure.redis;

import org.giglab.live.domain.model.Room;
import org.giglab.live.domain.repository.RoomRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

/**
 * @author : JAKE
 * @date : 26. 1. 11.
 */
@Repository
public class RedisRoomRepository implements RoomRepository {

  private static final String ROOM_KEY_PREFIX = "LIVE:ROOM";
  private static final Duration ROOM_TTL = Duration.ofDays(7);

  private final RedisTemplate<String, Object> redisTemplate;

  public RedisRoomRepository(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  @Override
  public Room save(Room room) {
    String key = String.format("%s:%s", ROOM_KEY_PREFIX, room.getRoomId());
    redisTemplate.opsForValue().set(key, room, ROOM_TTL);
    return room;
  }
}
