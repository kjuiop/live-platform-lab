package org.giglab.live.commerce.core.campaign.application.usecase;

import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.campaign.application.dto.BroadcastStatusResult;
import org.giglab.live.commerce.core.campaign.application.port.persistence.CampaignStorePort;
import org.giglab.live.commerce.core.campaign.domain.entity.Campaign;
import org.giglab.live.commerce.core.campaign.domain.exception.CampaignDomainException;
import org.giglab.live.commerce.core.campaign.domain.exception.CampaignErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StartCampaignUseCase {

  private final CampaignStorePort campaignStorePort;

  public BroadcastStatusResult execute(Long campaignId) {
    Campaign campaign =
        campaignStorePort
            .findEntityById(campaignId)
            .orElseThrow(
                () ->
                    new CampaignDomainException(
                        CampaignErrorCode.NOT_FOUND,
                        String.format("캠페인 ID %d 를 찾을 수 없습니다.", campaignId)));
    campaign.start();
    return new BroadcastStatusResult(
        campaign.getTitle(),
        campaign.getStatus(),
        campaign.getStartedAt(),
        null,
        campaign.getChatRoomId());
  }
}
