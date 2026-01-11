package org.giglab.live.application.dto;

import java.time.LocalDateTime;

/**
 * @author : JAKE
 * @date : 26. 1. 11.
 */
public record CreateRoomResponse(
  String roomId,
  String title,
  LocalDateTime createdAt,
  LocalDateTime updatedAt) {
}
