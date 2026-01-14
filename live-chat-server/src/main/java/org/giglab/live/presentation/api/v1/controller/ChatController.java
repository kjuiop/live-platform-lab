package org.giglab.live.presentation.api.v1.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.application.dto.Message;
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
  public Message sendMessage(Message message) {
    log.info(
        "메시지 수신 - Channel: {}, Sender: {}, Message: {}",
        message.getChannelId(),
        message.getSender(),
        message.getMessage());
    operations.convertAndSend("/sub/channel/" + message.getChannelId(), message);
    return message;
  }
}
