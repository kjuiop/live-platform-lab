package org.giglab.live.commerce.core.campaign.application.usecase;

import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.campaign.application.dto.SaveCampaignReportCommand;
import org.giglab.live.commerce.core.campaign.application.port.persistence.CampaignReportStorePort;
import org.giglab.live.commerce.core.campaign.application.port.persistence.CampaignStorePort;
import org.giglab.live.commerce.core.campaign.domain.entity.Campaign;
import org.giglab.live.commerce.core.campaign.domain.entity.CampaignReport;
import org.giglab.live.commerce.core.campaign.domain.exception.CampaignReportDomainException;
import org.giglab.live.commerce.core.campaign.domain.exception.CampaignReportErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SaveCampaignReportUseCase {

  private final CampaignStorePort campaignStorePort;
  private final CampaignReportStorePort campaignReportStorePort;

  public void execute(SaveCampaignReportCommand command) {
    Campaign campaign =
        campaignStorePort
            .findEntityByChatRoomId(command.roomId())
            .orElseThrow(
                () ->
                    new CampaignReportDomainException(CampaignReportErrorCode.CAMPAIGN_NOT_FOUND));

    CampaignReport report =
        CampaignReport.builder()
            .campaignId(campaign.getId())
            .roomId(command.roomId())
            .totalViewers(command.totalViewers())
            .peakConcurrent(command.peakConcurrent())
            .avgDurationSeconds(command.avgDurationSeconds())
            .totalMessages(command.totalMessages())
            .totalQuestions(command.totalQuestions())
            .aiAnswerCount(command.aiAnswerCount())
            .aiReportText(command.aiReportText())
            .startedAt(command.startedAt())
            .endedAt(command.endedAt())
            .build();

    campaignReportStorePort.store(report);
  }
}
