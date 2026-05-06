package org.giglab.live.application.port.persistence;

import java.util.Optional;
import org.giglab.live.application.dto.action.ActiveBanner;

public interface BannerStatePort {

  void save(String roomId, ActiveBanner banner);

  void clear(String roomId);

  Optional<ActiveBanner> getActiveBanner(String roomId);
}
