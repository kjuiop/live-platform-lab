package org.giglab.live.application.port.persistence;

import java.util.Optional;
import org.giglab.live.application.dto.action.ActiveBanner;
import org.giglab.live.application.dto.action.ShowBannerResponse;

public interface BannerStatePort {

  void save(String roomId, ShowBannerResponse response);

  void clear(String roomId);

  Optional<ActiveBanner> getActiveBanner(String roomId);
}
