package org.giglab.live.application.dto;

import java.time.LocalDateTime;

public record CreateRoomResponse(
    String roomId, String title, LocalDateTime createdAt, LocalDateTime updatedAt) {}
