package org.giglab.live.commerce.core.campaign.application.dto;

import org.giglab.live.commerce.core.campaign.domain.entity.types.BroadcastStatusType;

public record CampaignPageQuery(int page, int size, String keyword, BroadcastStatusType status) {

  public long offset() {
    return (long) (page - 1) * size;
  }
}
