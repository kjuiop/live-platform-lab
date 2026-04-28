package org.giglab.live.application.port.persistence;

import java.util.Optional;
import org.giglab.live.application.dto.viewer.ViewerContext;

public interface ViewerSessionPort {

  void saveUserId(String sessionId, String userId);

  void addViewer(String roomId, String sessionId);

  Optional<ViewerContext> getAndRemoveViewer(String sessionId);

  long getViewerCount(String roomId);

  void saveSession(ViewerContext ctx);
}
