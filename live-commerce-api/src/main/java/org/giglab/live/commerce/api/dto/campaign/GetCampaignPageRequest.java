package org.giglab.live.commerce.api.dto.campaign;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.giglab.live.commerce.core.campaign.domain.entity.types.BroadcastStatusType;

public record GetCampaignPageRequest(
    @Min(1) Integer page,
    @Min(1) @Max(100) Integer size,
    String keyword,
    BroadcastStatusType status) {

  public int pageOrDefault() {
    return page != null ? page : 1;
  }

  public int sizeOrDefault() {
    return size != null ? size : 20;
  }
}
