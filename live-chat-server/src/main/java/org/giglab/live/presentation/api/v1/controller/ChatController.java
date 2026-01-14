package org.giglab.live.presentation.api.v1.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.application.dto.ChatMessageRequest;
import org.giglab.live.application.dto.ChatMessageResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

/**
 * @author : JAKE
 * @date : 26. 1. 13.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

  private final SimpMessageSendingOperations operations;

  @MessageMapping("/chat.send")
  public ChatMessageResponse sendMessage(ChatMessageRequest request) {
    log.info(
        "메시지 수신 - Channel: {}, Sender: {}, Message: {}",
        request.getChannelId(),
        request.getSender(),
        request.getMessage());

    ChatMessageResponse response = ChatMessageResponse.from(request);

    operations.convertAndSend("/sub/channel/" + response.getChannelId(), response);

    return response;
  }
}
