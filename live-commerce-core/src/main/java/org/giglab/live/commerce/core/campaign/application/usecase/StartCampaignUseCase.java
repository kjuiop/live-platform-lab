package org.giglab.live.commerce.core.campaign.application.usecase;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.commerce.core.campaign.application.dto.BroadcastStatusResult;
import org.giglab.live.commerce.core.campaign.application.port.external.ChatRoomCreatePort;
import org.giglab.live.commerce.core.campaign.application.port.persistence.CampaignStorePort;
import org.giglab.live.commerce.core.campaign.domain.entity.Campaign;
import org.giglab.live.commerce.core.campaign.domain.exception.CampaignDomainException;
import org.giglab.live.commerce.core.campaign.domain.exception.CampaignErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StartCampaignUseCase {

  private final CampaignStorePort campaignStorePort;
  private final ChatRoomCreatePort chatRoomCreatePort;

  public BroadcastStatusResult execute(Long campaignId) {
    Optional<Campaign> findCampaign = campaignStorePort.findEntityById(campaignId);
    if (findCampaign.isEmpty()) {
      throw new CampaignDomainException(
          CampaignErrorCode.NOT_FOUND, String.format("캠페인 ID %d 를 찾을 수 없습니다.", campaignId));
    }
    Campaign campaign = findCampaign.get();
    campaign.start();

    if (campaign.getChatRoomId() == null) {
      try {
        String roomId = chatRoomCreatePort.createRoom(campaign.getTitle());
        campaign.assignChatRoom(roomId);
      } catch (Exception e) {
        log.warn("채팅방 생성 실패 - campaignId={}, error={}", campaignId, e.getMessage());
      }
    }

    return new BroadcastStatusResult(
        campaign.getStatus(), campaign.getStartedAt(), null, campaign.getChatRoomId());
  }
}
