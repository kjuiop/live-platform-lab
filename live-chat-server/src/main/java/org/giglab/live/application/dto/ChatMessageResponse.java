package org.giglab.live.application.dto;

import java.time.Instant;
import java.util.Map;
import org.giglab.live.domain.model.ChatMessage;

public record ChatMessageResponse(
    String id,
    String roomId,
    String action,
    String senderUserId,
    String senderNickname,
    Map<String, Object> payload,
    Instant sentAt,
    long seq) {

  public static ChatMessageResponse from(ChatMessage msg) {
    return new ChatMessageResponse(
        msg.getId(),
        msg.getRoomId(),
        msg.getAction(),
        msg.getSenderUserId(),
        msg.getSenderNickname(),
        msg.getPayload(),
        msg.getSentAt(),
        msg.getSeq());
  }
}
