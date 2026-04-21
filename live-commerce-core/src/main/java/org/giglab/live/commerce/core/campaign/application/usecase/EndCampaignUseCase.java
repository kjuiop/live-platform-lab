package org.giglab.live.commerce.core.campaign.application.usecase;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.campaign.application.port.persistence.CampaignStorePort;
import org.giglab.live.commerce.core.campaign.domain.entity.Campaign;
import org.giglab.live.commerce.core.campaign.domain.exception.CampaignDomainException;
import org.giglab.live.commerce.core.campaign.domain.exception.CampaignErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EndCampaignUseCase {

  private final CampaignStorePort campaignStorePort;

  public void execute(Long campaignId) {
    Optional<Campaign> findCampaign = campaignStorePort.findEntityById(campaignId);
    if (findCampaign.isEmpty()) {
      throw new CampaignDomainException(
          CampaignErrorCode.NOT_FOUND, String.format("캠페인 ID %d 를 찾을 수 없습니다.", campaignId));
    }
    Campaign campaign = findCampaign.get();
    campaign.end();
  }
}
