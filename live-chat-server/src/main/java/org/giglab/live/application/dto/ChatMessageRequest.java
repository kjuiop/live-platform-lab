package org.giglab.live.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequest {
  private Long channelId;
  private String sender;
  private String message;
}
