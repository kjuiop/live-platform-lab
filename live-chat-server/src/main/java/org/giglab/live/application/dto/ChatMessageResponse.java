package org.giglab.live.application.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatMessageResponse {
  private Long channelId;
  private String sender;
  private String message;
  private LocalDateTime sentAt;

  public static ChatMessageResponse from(ChatMessageRequest request) {
    return new ChatMessageResponse(
        request.getChannelId(), request.getSender(), request.getMessage(), LocalDateTime.now());
  }
}
