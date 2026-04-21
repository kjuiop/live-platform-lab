package org.giglab.live.commerce.api.dto.campaign;

import java.time.LocalDateTime;

public record BroadcastStatusResponse(
    String status, LocalDateTime startedAt, LocalDateTime endedAt) {}
