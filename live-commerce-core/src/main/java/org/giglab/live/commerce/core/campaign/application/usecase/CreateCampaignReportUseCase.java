package org.giglab.live.commerce.core.campaign.application.usecase;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignReportCommand;
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
public class CreateCampaignReportUseCase {

  private final CampaignStorePort campaignStorePort;
  private final CampaignReportStorePort campaignReportStorePort;

  public void execute(Long campaignId, CreateCampaignReportCommand command) {

    Optional<Campaign> findCampaign = campaignStorePort.findEntityById(campaignId);
    if (findCampaign.isEmpty()) {
      throw new CampaignReportDomainException(CampaignReportErrorCode.CAMPAIGN_NOT_FOUND);
    }

    Campaign campaign = findCampaign.get();

    CampaignReport report =
        campaignReportStorePort
            .findByRoomId(command.chatRoomId())
            .orElseGet(
                () ->
                    CampaignReport.create(
                        campaign.getId(),
                        command.chatRoomId(),
                        command.totalViewers(),
                        command.peakConcurrent(),
                        command.avgDurationSeconds(),
                        command.totalMessages(),
                        command.totalQuestions(),
                        command.aiAnswerCount(),
                        command.aiReportText(),
                        command.startedAt(),
                        command.endedAt(),
                        command.unansweredQuestions()));

    if (report.getId() != null) {
      report.update(
          command.totalViewers(),
          command.peakConcurrent(),
          command.avgDurationSeconds(),
          command.totalMessages(),
          command.totalQuestions(),
          command.aiAnswerCount(),
          command.aiReportText(),
          command.startedAt(),
          command.endedAt(),
          command.unansweredQuestions());
    }

    campaignReportStorePort.store(report);
  }
}
