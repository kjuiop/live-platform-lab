package org.giglab.live.application.dto;

import java.time.Instant;

public record CreateRoomResponse(
    String roomId, String title, Instant createdAt, Instant updatedAt) {}
