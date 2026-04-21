package org.giglab.live.commerce.core.campaign.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.global.exception.DomainErrorCode;

@Getter
@RequiredArgsConstructor
public enum CampaignErrorCode implements DomainErrorCode {
  NOT_FOUND("CAMPAIGN-4401", "존재하지 않는 캠페인입니다.");

  private final String code;
  private final String message;
}
