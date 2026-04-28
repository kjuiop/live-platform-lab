package org.giglab.live.commerce.core.campaign.application.dto;

import java.util.List;

public record GetCampaignListResult(List<CampaignSummary> items, Long nextCursor, boolean hasNext) {

  public static GetCampaignListResult of(List<CampaignSummary> fetched, int requestedSize) {
    boolean hasNext = fetched.size() > requestedSize;
    List<CampaignSummary> items = hasNext ? fetched.subList(0, requestedSize) : fetched;
    Long nextCursor = hasNext ? items.getLast().id() : null;
    return new GetCampaignListResult(items, nextCursor, hasNext);
  }
}
