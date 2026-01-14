package org.giglab.live.application.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** 서버 → 클라이언트로 전파되는 채팅 메시지 응답 DTO. */
@Getter
@AllArgsConstructor
public class ChatMessageResponse {
  private Long channelId;
  private String sender;
  private String message;
  private Instant sentAt;

  /**
   * ChatMessageRequest로부터 ChatMessageResponse를 생성합니다.
   *
   * @param request 요청 DTO
   * @return 응답 DTO (sentAt은 현재 시간으로 설정됨)
   */
  public static ChatMessageResponse from(ChatMessageRequest request) {
    return new ChatMessageResponse(
        request.getChannelId(), request.getSender(), request.getMessage(), Instant.now());
  }
}
