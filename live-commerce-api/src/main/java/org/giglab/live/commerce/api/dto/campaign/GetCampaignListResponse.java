package org.giglab.live.commerce.api.dto.campaign;

import java.util.List;

public record GetCampaignListResponse(
    List<CampaignSummaryItem> items, Long nextCursor, boolean hasNext) {}
