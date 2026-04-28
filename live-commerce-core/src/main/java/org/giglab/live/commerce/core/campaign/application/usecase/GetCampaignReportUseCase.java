package org.giglab.live.commerce.core.campaign.application.usecase;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.campaign.application.dto.GetCampaignReportResult;
import org.giglab.live.commerce.core.campaign.application.port.persistence.CampaignReportStorePort;
import org.giglab.live.commerce.core.campaign.domain.entity.CampaignReport;
import org.giglab.live.commerce.core.campaign.domain.exception.CampaignReportDomainException;
import org.giglab.live.commerce.core.campaign.domain.exception.CampaignReportErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetCampaignReportUseCase {

  private final CampaignReportStorePort campaignReportStorePort;

  public GetCampaignReportResult execute(Long campaignId) {
    Optional<CampaignReport> findReport = campaignReportStorePort.findByCampaignId(campaignId);
    if (findReport.isEmpty()) {
      throw new CampaignReportDomainException(CampaignReportErrorCode.REPORT_NOT_FOUND);
    }

    CampaignReport report = findReport.get();
    return GetCampaignReportResult.from(report);
  }
}
