package org.giglab.live.application.dto.room;

import java.time.Instant;

public record CreateRoomResponse(
    String roomId, String title, Instant createdAt, Instant updatedAt) {}
