package org.giglab.live.commerce.core.campaign.application.usecase;

import java.util.Optional;
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

    Optional<Campaign> findCampaign = campaignStorePort.findEntityByChatRoomId(command.roomId());
    if (findCampaign.isEmpty()) {
      throw new CampaignReportDomainException(CampaignReportErrorCode.CAMPAIGN_NOT_FOUND);
    }

    Campaign campaign = findCampaign.get();

    CampaignReport report =
        CampaignReport.create(
            campaign.getId(),
            command.roomId(),
            command.totalViewers(),
            command.peakConcurrent(),
            command.avgDurationSeconds(),
            command.totalMessages(),
            command.totalQuestions(),
            command.aiAnswerCount(),
            command.aiReportText(),
            command.startedAt(),
            command.endedAt());

    campaignReportStorePort.store(report);
  }
}
