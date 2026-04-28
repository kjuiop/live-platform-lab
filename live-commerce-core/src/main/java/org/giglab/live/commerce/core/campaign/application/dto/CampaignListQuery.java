package org.giglab.live.commerce.core.campaign.application.dto;

import org.giglab.live.commerce.core.campaign.domain.entity.types.BroadcastStatusType;

public record CampaignListQuery(Long cursor, int size, String keyword, BroadcastStatusType status) {
  public int fetchSize() {
    return size + 1;
  }
}
