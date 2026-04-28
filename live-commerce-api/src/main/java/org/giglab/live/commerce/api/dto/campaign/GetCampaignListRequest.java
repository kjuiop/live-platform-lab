package org.giglab.live.commerce.api.dto.campaign;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.giglab.live.commerce.core.campaign.domain.entity.types.BroadcastStatusType;

public record GetCampaignListRequest(
    Long cursor, @Min(1) @Max(100) Integer size, String keyword, BroadcastStatusType status) {}
