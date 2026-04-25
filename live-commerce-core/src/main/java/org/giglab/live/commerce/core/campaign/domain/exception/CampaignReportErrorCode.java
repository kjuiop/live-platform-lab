package org.giglab.live.commerce.core.campaign.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.global.exception.DomainErrorCode;

@Getter
@RequiredArgsConstructor
public enum CampaignReportErrorCode implements DomainErrorCode {
  CAMPAIGN_NOT_FOUND("CAMPAIGN-REPORT-4401", "채팅방에 연결된 캠페인을 찾을 수 없습니다.");

  private final String code;
  private final String message;
}
