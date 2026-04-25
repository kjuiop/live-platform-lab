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
import org.giglab.live.commerce.core.campaign.application.port.external.ChatRoomDeletePort;
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
  private final ChatRoomDeletePort chatRoomDeletePort;

  public GetCampaignListResult getList(CampaignListQuery query) {
    return getCampaignListUseCase.execute(query);
  }

  public GetCampaignResult getDetail(Long campaignId) {
    return getCampaignUseCase.execute(campaignId);
  }

  public BroadcastStatusResult start(Long campaignId) {
    // TX 1: 방송 시작 커밋 — DB 커넥션 즉시 반환
    BroadcastStatusResult result = startCampaignUseCase.execute(campaignId);

    // HTTP: 채팅방 생성 — 트랜잭션 외부
    if (result.chatRoomId() == null) {
      try {
        String roomId = chatRoomCreatePort.createRoom(result.title());

        // TX 2: chatRoomId 저장 커밋
        assignChatRoomUseCase.execute(campaignId, roomId);
        return new BroadcastStatusResult(
            result.title(), result.status(), result.startedAt(), null, roomId);
      } catch (Exception e) {
        log.warn("채팅방 생성 실패 - campaignId={}", campaignId, e);
      }
    }
    return result;
  }

  public BroadcastStatusResult end(Long campaignId) {
    // TX 1: 방송 종료 커밋 — DB 커넥션 즉시 반환
    BroadcastStatusResult result = endCampaignUseCase.execute(campaignId);

    // HTTP: 채팅방 삭제 — chatRoomId는 DB에 유지 (stats 조회 용도)
    if (result.chatRoomId() != null) {
      try {
        chatRoomDeletePort.deleteRoom(result.chatRoomId());
      } catch (Exception e) {
        log.warn("채팅방 삭제 실패 (chat-server) - campaignId={}", campaignId, e);
      }
    }

    return result;
  }

  public CreateCampaignResult create(CreateCampaignCommand request) {
    return createCampaignUseCase.execute(request);
  }
}
