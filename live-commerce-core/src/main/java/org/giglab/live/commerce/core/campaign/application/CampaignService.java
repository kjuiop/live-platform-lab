package org.giglab.live.commerce.core.campaign.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.commerce.core.campaign.application.dto.BroadcastStatusResult;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignListQuery;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignPageQuery;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignCommand;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignReportCommand;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignResult;
import org.giglab.live.commerce.core.campaign.application.dto.GenerateAiReportCommand;
import org.giglab.live.commerce.core.campaign.application.dto.GetCampaignListResult;
import org.giglab.live.commerce.core.campaign.application.dto.GetCampaignPageResult;
import org.giglab.live.commerce.core.campaign.application.dto.GetCampaignReportResult;
import org.giglab.live.commerce.core.campaign.application.dto.GetCampaignResult;
import org.giglab.live.commerce.core.campaign.application.port.external.ChatRoomCreatePort;
import org.giglab.live.commerce.core.campaign.application.port.external.ChatRoomDeletePort;
import org.giglab.live.commerce.core.campaign.application.usecase.CreateCampaignReportUseCase;
import org.giglab.live.commerce.core.campaign.application.usecase.CreateCampaignUseCase;
import org.giglab.live.commerce.core.campaign.application.usecase.EndCampaignUseCase;
import org.giglab.live.commerce.core.campaign.application.usecase.GenerateAiReportUseCase;
import org.giglab.live.commerce.core.campaign.application.usecase.GetCampaignListUseCase;
import org.giglab.live.commerce.core.campaign.application.usecase.GetCampaignPageUseCase;
import org.giglab.live.commerce.core.campaign.application.usecase.GetCampaignReportUseCase;
import org.giglab.live.commerce.core.campaign.application.usecase.GetCampaignUseCase;
import org.giglab.live.commerce.core.campaign.application.usecase.StartCampaignUseCase;
import org.giglab.live.commerce.core.campaign.domain.exception.CampaignDomainException;
import org.giglab.live.commerce.core.campaign.domain.exception.CampaignErrorCode;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignService {

  private final GetCampaignListUseCase getCampaignListUseCase;
  private final GetCampaignPageUseCase getCampaignPageUseCase;
  private final GetCampaignUseCase getCampaignUseCase;
  private final CreateCampaignUseCase createCampaignUseCase;
  private final StartCampaignUseCase startCampaignUseCase;
  private final EndCampaignUseCase endCampaignUseCase;
  private final ChatRoomCreatePort chatRoomCreatePort;
  private final ChatRoomDeletePort chatRoomDeletePort;
  private final CreateCampaignReportUseCase createCampaignReportUseCase;
  private final GenerateAiReportUseCase generateAiReportUseCase;
  private final GetCampaignReportUseCase getCampaignReportUseCase;

  public GetCampaignListResult getList(CampaignListQuery query) {
    return getCampaignListUseCase.execute(query);
  }

  public GetCampaignPageResult getPage(CampaignPageQuery query) {
    return getCampaignPageUseCase.execute(query);
  }

  public GetCampaignResult getDetail(Long campaignId) {
    return getCampaignUseCase.execute(campaignId);
  }

  public BroadcastStatusResult start(Long campaignId) {
    // 캠페인 제목 조회 (TX 외부 — 읽기 전용)
    String title = getCampaignUseCase.execute(campaignId).title();

    // HTTP: 채팅방 생성 — 실패 시 도메인 예외로 변환해 방송 시작 중단
    String roomId;
    try {
      roomId = chatRoomCreatePort.createRoom(title);
    } catch (Exception e) {
      log.error("채팅방 생성 실패 - campaignId={}", campaignId, e);
      throw new CampaignDomainException(CampaignErrorCode.CHAT_ROOM_CREATE_FAILED, e.getMessage());
    }

    // TX: 방송 시작 + chatRoomId 저장 — 하나의 커밋
    return startCampaignUseCase.execute(campaignId, roomId);
  }

  public BroadcastStatusResult end(Long campaignId) {
    // TX 1: 방송 종료 커밋 — DB 커넥션 즉시 반환
    BroadcastStatusResult result = endCampaignUseCase.execute(campaignId);

    // HTTP: 채팅방 삭제 + AI 리포트 생성 — 둘 다 @Async, end() 응답을 블로킹하지 않음
    if (result.chatRoomId() != null) {
      chatRoomDeletePort.deleteRoom(result.chatRoomId());
      generateAiReportUseCase.execute(new GenerateAiReportCommand(campaignId, result.chatRoomId()));
    }

    return result;
  }

  public CreateCampaignResult create(CreateCampaignCommand request) {
    return createCampaignUseCase.execute(request);
  }

  public void saveCampaignReport(Long campaignId, CreateCampaignReportCommand command) {
    createCampaignReportUseCase.execute(campaignId, command);
  }

  public GetCampaignReportResult getCampaignReport(Long campaignId) {
    return getCampaignReportUseCase.execute(campaignId);
  }
}
