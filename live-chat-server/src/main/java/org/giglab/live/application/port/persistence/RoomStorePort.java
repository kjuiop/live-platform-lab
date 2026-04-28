package org.giglab.live.application.port.persistence;

import org.giglab.live.domain.model.Room;

public interface RoomStorePort {

  Room save(Room room);

  void deleteById(String roomId);
}
