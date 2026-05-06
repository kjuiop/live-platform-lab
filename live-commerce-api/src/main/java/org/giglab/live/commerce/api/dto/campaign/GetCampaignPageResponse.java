package org.giglab.live.commerce.api.dto.campaign;

import java.util.List;

public record GetCampaignPageResponse(
    List<CampaignSummaryItem> items, int page, int totalPages, long totalCount, boolean hasNext) {}
