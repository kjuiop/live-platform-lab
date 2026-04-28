package org.giglab.live.application.port.persistence;

public interface ViewerSessionPort {

  void saveUserId(String sessionId, String userId);
}
