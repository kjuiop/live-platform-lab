package org.giglab.live.infrastructure.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.giglab.live.application.dto.action.ActiveBanner;
import org.giglab.live.application.dto.action.ShowBannerResponse;
import org.giglab.live.application.port.persistence.BannerStatePort;
import org.giglab.live.infrastructure.redis.RedisBannerRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisBannerStateAdapter implements BannerStatePort {

  private final RedisBannerRepository redisBannerRepository;

  @Override
  public void save(String roomId, ShowBannerResponse response) {
    redisBannerRepository.save(
        roomId, new ActiveBanner(response.productId(), response.productName()));
  }

  @Override
  public void clear(String roomId) {
    redisBannerRepository.delete(roomId);
  }

  @Override
  public Optional<ActiveBanner> getActiveBanner(String roomId) {
    return redisBannerRepository.find(roomId);
  }
}
