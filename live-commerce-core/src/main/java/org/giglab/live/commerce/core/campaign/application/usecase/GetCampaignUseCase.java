package org.giglab.live.commerce.core.campaign.application.usecase;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.campaign.application.dto.GetCampaignResult;
import org.giglab.live.commerce.core.campaign.application.port.persistence.CampaignQueryPort;
import org.giglab.live.commerce.core.campaign.domain.exception.CampaignDomainException;
import org.giglab.live.commerce.core.campaign.domain.exception.CampaignErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetCampaignUseCase {

  private final CampaignQueryPort campaignQueryPort;

  public GetCampaignResult execute(Long campaignId) {
    Optional<GetCampaignResult> findDetail = campaignQueryPort.findById(campaignId);
    if (findDetail.isEmpty()) {
      throw new CampaignDomainException(
          CampaignErrorCode.NOT_FOUND, String.format("존재하지 않는 캠페인 %d 입니다.", campaignId));
    }
    return findDetail.get();
  }
}
