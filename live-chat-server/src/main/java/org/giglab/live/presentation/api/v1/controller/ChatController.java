package org.giglab.live.presentation.api.v1.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.application.dto.ChatMessageRequest;
import org.giglab.live.application.dto.ChatMessageResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

  private static final String SUBSCRIBE_PREFIX = "/sub/room/";

  private final SimpMessageSendingOperations operations;

  /**
   * 클라이언트로부터 채팅 메시지를 수신하고 브로드캐스트합니다.
   *
   * @param request 채팅 메시지 요청
   * @return 채팅 메시지 응답 (sentAt 포함)
   */
  @MessageMapping("/chat.send")
  public ChatMessageResponse sendMessage(@Valid ChatMessageRequest request) {
    String destination = SUBSCRIBE_PREFIX + request.getRoomId();
    ChatMessageResponse response = ChatMessageResponse.from(request);
    operations.convertAndSend(destination, response);
    return response;
  }
}
