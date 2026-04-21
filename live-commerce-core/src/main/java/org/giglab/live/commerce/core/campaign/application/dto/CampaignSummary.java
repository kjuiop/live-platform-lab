package org.giglab.live.commerce.core.campaign.application.dto;

import java.time.LocalDateTime;
import org.giglab.live.commerce.core.campaign.domain.entity.types.BroadcastStatusType;

public record CampaignSummary(
    Long id,
    String title,
    String description,
    BroadcastStatusType status,
    LocalDateTime scheduledAt,
    int productCount) {}
