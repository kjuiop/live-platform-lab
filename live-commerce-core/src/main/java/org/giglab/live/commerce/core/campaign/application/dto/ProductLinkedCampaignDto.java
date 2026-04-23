package org.giglab.live.commerce.core.campaign.application.dto;

import java.time.LocalDateTime;
import org.giglab.live.commerce.core.campaign.domain.entity.types.BroadcastStatusType;

public record ProductLinkedCampaignDto(
    Long campaignId,
    String title,
    BroadcastStatusType status,
    LocalDateTime scheduledAt,
    LocalDateTime startedAt,
    LocalDateTime endedAt) {}
