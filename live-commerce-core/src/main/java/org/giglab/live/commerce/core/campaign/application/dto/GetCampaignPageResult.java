package org.giglab.live.commerce.core.campaign.application.dto;

import java.util.List;
import org.giglab.live.commerce.core.campaign.domain.exception.CampaignDomainException;
import org.giglab.live.commerce.core.campaign.domain.exception.CampaignErrorCode;

public record GetCampaignPageResult(
    List<CampaignSummary> items,
    int page,
    int size,
    long totalCount,
    int totalPages,
    boolean hasNext) {

  public static GetCampaignPageResult of(
      List<CampaignSummary> items, int page, int size, long totalCount) {
    if (size <= 0) {
      throw new CampaignDomainException(CampaignErrorCode.INVALID_PAGE_SIZE);
    }
    int totalPages = (int) Math.ceil((double) totalCount / size);
    boolean hasNext = page < totalPages;
    return new GetCampaignPageResult(items, page, size, totalCount, totalPages, hasNext);
  }
}
