package org.giglab.live.commerce.core.campaign.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.commerce.core.campaign.application.dto.BroadcastStatusResult;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignListQuery;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignCommand;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignResult;
import org.giglab.live.commerce.core.campaign.application.dto.GetCampaignListResult;
import org.giglab.live.commerce.core.campaign.application.dto.GetCampaignResult;
import org.giglab.live.commerce.core.campaign.application.port.external.ChatRoomCreatePort;
import org.giglab.live.commerce.core.campaign.application.usecase.AssignChatRoomUseCase;
import org.giglab.live.commerce.core.campaign.application.usecase.CreateCampaignUseCase;
import org.giglab.live.commerce.core.campaign.application.usecase.EndCampaignUseCase;
import org.giglab.live.commerce.core.campaign.application.usecase.GetCampaignListUseCase;
import org.giglab.live.commerce.core.campaign.application.usecase.GetCampaignUseCase;
import org.giglab.live.commerce.core.campaign.application.usecase.StartCampaignUseCase;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignService {

  private final GetCampaignListUseCase getCampaignListUseCase;
  private final GetCampaignUseCase getCampaignUseCase;
  private final CreateCampaignUseCase createCampaignUseCase;
  private final StartCampaignUseCase startCampaignUseCase;
  private final EndCampaignUseCase endCampaignUseCase;
  private final AssignChatRoomUseCase assignChatRoomUseCase;
  private final ChatRoomCreatePort chatRoomCreatePort;

  public GetCampaignListResult getList(CampaignListQuery query) {
    return getCampaignListUseCase.execute(query);
  }

  public GetCampaignResult getDetail(Long campaignId) {
    return getCampaignUseCase.execute(campaignId);
  }

  public BroadcastStatusResult start(Long campaignId) {
    BroadcastStatusResult result = startCampaignUseCase.execute(campaignId);
    if (result.chatRoomId() != null) {
      return result;
    }
    String roomId = chatRoomCreatePort.createRoom(result.title());
    assignChatRoomUseCase.execute(campaignId, roomId);
    return new BroadcastStatusResult(
        result.title(), result.status(), result.startedAt(), null, roomId);
  }

  public BroadcastStatusResult end(Long campaignId) {
    return endCampaignUseCase.execute(campaignId);
  }

  public CreateCampaignResult create(CreateCampaignCommand request) {
    return createCampaignUseCase.execute(request);
  }
}
