package org.giglab.live.commerce.core.campaign.application;

import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.campaign.application.dto.SaveCampaignReportCommand;
import org.giglab.live.commerce.core.campaign.application.usecase.SaveCampaignReportUseCase;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampaignReportService {

  private final SaveCampaignReportUseCase saveCampaignReportUseCase;

  public void save(SaveCampaignReportCommand command) {
    saveCampaignReportUseCase.execute(command);
  }
}
