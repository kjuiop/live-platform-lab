package org.giglab.live.commerce.api.dto.campaign;

import java.time.LocalDateTime;
import java.util.List;

public record GetCampaignResponse(
    Long id,
    String title,
    String description,
    String status,
    LocalDateTime scheduledAt,
    LocalDateTime startedAt,
    LocalDateTime endedAt,
    String chatRoomId,
    List<CampaignProductItem> campaignProducts) {}
