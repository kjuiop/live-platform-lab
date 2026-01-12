package org.giglab.live.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.giglab.live.domain.model.Room;

/**
 * @author : JAKE
 * @date : 26. 1. 11.
 */
public interface RoomRepository {

  Room save(Room room);

  List<String> findLatestRoomIds(int limit);

  Stream<Room> getRoomsByIds(List<String> roomIds);

  Optional<Room> findById(String roomId);
}
