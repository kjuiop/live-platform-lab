package org.giglab.live.commerce.core.campaign.domain.exception;

import org.giglab.live.commerce.core.global.exception.DomainException;

public class CampaignDomainException extends DomainException {

  public CampaignDomainException(CampaignErrorCode errorCode) {
    super(errorCode);
  }

  public CampaignDomainException(CampaignErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
