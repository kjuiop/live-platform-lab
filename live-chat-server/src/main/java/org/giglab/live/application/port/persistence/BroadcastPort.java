package org.giglab.live.application.port.persistence;

public interface BroadcastPort {

  void publish(String roomId, Object payload);
}
