package org.giglab.live.commerce.api.facade;

import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.api.dto.broadcast.SaveCampaignReportRequest;
import org.giglab.live.commerce.core.campaign.application.CampaignReportService;
import org.giglab.live.commerce.core.campaign.application.dto.SaveCampaignReportCommand;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampaignReportFacade {

  private final CampaignReportService campaignReportService;

  public void save(SaveCampaignReportRequest request) {
    campaignReportService.save(
        new SaveCampaignReportCommand(
            request.roomId(),
            request.totalViewers(),
            request.peakConcurrent(),
            request.avgDurationSeconds(),
            request.totalMessages(),
            request.totalQuestions(),
            request.aiAnswerCount(),
            request.aiReportText(),
            request.startedAt(),
            request.endedAt()));
  }
}
