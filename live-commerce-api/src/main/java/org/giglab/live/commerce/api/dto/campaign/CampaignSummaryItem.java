package org.giglab.live.commerce.api.dto.campaign;

import java.time.LocalDateTime;

public record CampaignSummaryItem(
    Long id,
    String title,
    String description,
    String status,
    LocalDateTime scheduledAt,
    long productCount) {}
