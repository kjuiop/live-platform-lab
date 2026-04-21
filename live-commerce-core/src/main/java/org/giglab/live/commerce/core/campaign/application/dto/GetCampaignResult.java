package org.giglab.live.commerce.core.campaign.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.giglab.live.commerce.core.campaign.domain.entity.types.BroadcastStatusType;

public record GetCampaignResult(
    Long id,
    String title,
    String description,
    BroadcastStatusType status,
    LocalDateTime scheduledAt,
    LocalDateTime startedAt,
    LocalDateTime endedAt,
    List<CampaignProductDto> campaignProducts) {}
