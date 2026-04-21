package org.giglab.live.commerce.core.campaign.application.dto;

import java.time.LocalDateTime;
import org.giglab.live.commerce.core.campaign.domain.entity.types.BroadcastStatusType;

public record BroadcastStatusResult(
    BroadcastStatusType status, LocalDateTime startedAt, LocalDateTime endedAt) {}
