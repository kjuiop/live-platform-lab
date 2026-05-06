package org.giglab.live.commerce.api.dto.campaign;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetCampaignListResponse(
    List<CampaignSummaryItem> items,
    Long nextCursor,
    Boolean hasNext,
    Integer page,
    Integer totalPages,
    Long totalCount) {

  public static GetCampaignListResponse ofCursor(
      List<CampaignSummaryItem> items, Long nextCursor, boolean hasNext) {
    return new GetCampaignListResponse(items, nextCursor, hasNext, null, null, null);
  }

  public static GetCampaignListResponse ofOffset(
      List<CampaignSummaryItem> items, int page, int totalPages, long totalCount) {
    boolean hasNext = page < totalPages;
    return new GetCampaignListResponse(items, null, hasNext, page, totalPages, totalCount);
  }
}
