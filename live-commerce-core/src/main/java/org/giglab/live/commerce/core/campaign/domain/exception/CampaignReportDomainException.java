package org.giglab.live.commerce.core.campaign.domain.exception;

import org.giglab.live.commerce.core.global.exception.DomainException;

public class CampaignReportDomainException extends DomainException {

  public CampaignReportDomainException(CampaignReportErrorCode errorCode) {
    super(errorCode);
  }
}
