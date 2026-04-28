package org.giglab.live.application.port.messaging;

public interface BroadcastPort {

  void publish(String roomId, Object payload);
}
