package org.giglab.live.infrastructure.adapter;

import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.giglab.live.application.port.persistence.RoomPort;
import org.giglab.live.domain.model.Room;
import org.giglab.live.infrastructure.redis.RedisRoomRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomAdapter implements RoomPort {

  private final RedisRoomRepository redisRoomRepository;

  @Override
  public Room save(Room room) {
    return redisRoomRepository.save(room);
  }

  @Override
  public List<String> findLatestRoomIds(int limit) {
    return redisRoomRepository.findLatestRoomIds(limit);
  }

  @Override
  public Stream<Room> getRoomsByIds(List<String> roomIds) {
    return redisRoomRepository.getRoomsByIds(roomIds);
  }

  @Override
  public void deleteById(String roomId) {
    redisRoomRepository.deleteById(roomId);
  }
}
