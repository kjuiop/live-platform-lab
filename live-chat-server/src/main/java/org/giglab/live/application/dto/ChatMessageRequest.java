package org.giglab.live.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 클라이언트 → 서버로 들어오는 채팅 메시지 요청 DTO. */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequest {
  private Long channelId;
  private String sender;
  private String message;
}
