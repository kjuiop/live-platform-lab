package org.giglab.live.commerce.api.dto.product;

import java.time.LocalDateTime;

public record LinkedCampaignItem(
    Long campaignId,
    String title,
    String status,
    LocalDateTime scheduledAt,
    LocalDateTime startedAt,
    LocalDateTime endedAt) {}
