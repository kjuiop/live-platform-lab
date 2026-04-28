package org.giglab.live.commerce.core.campaign.application.usecase;

import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.campaign.application.port.persistence.CampaignStorePort;
import org.giglab.live.commerce.core.campaign.domain.exception.CampaignDomainException;
import org.giglab.live.commerce.core.campaign.domain.exception.CampaignErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AssignChatRoomUseCase {

  private final CampaignStorePort campaignStorePort;

  public void execute(Long campaignId, String chatRoomId) {
    campaignStorePort
        .findEntityById(campaignId)
        .orElseThrow(
            () ->
                new CampaignDomainException(
                    CampaignErrorCode.NOT_FOUND,
                    String.format("캠페인 ID %d 를 찾을 수 없습니다.", campaignId)))
        .assignChatRoom(chatRoomId);
  }
}
