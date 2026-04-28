package org.giglab.live.application.port.persistence;

import java.util.List;
import java.util.stream.Stream;
import org.giglab.live.domain.model.Room;

public interface RoomPort {

  List<String> findLatestRoomIds(int limit);

  Stream<Room> getRoomsByIds(List<String> roomIds);

  Room save(Room room);

  void deleteById(String roomId);
}
