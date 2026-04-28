package org.giglab.live.application.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.application.dto.ChatMessageResponse;
import org.giglab.live.application.dto.action.ActionResponse;
import org.giglab.live.application.port.persistence.ChatMessagePort;
import org.giglab.live.domain.model.ChatMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {

  private static final int MAX_LIMIT = 200;

  private static final Set<String> SAVEABLE_ACTIONS =
      Set.of(
          ActionType.CHAT_MESSAGE.getKey(),
          ActionType.FAQ_QUESTION.getKey(),
          ActionType.FAQ_ANSWER.getKey(),
          ActionType.FAQ_ERROR.getKey());

  private final ChatMessagePort chatMessagePort;

  @Async("chatAsyncExecutor")
  public void saveIfNeeded(ActionResponse res) {
    if (!SAVEABLE_ACTIONS.contains(res.action())) {
      return;
    }
    try {
      ChatMessage message =
          ChatMessage.create(
              res.roomId(),
              res.action(),
              res.actor() != null ? res.actor().userId() : null,
              res.actor() != null ? res.actor().sender() : null,
              res.payload(),
              res.sentAt());

      chatMessagePort.save(message);
    } catch (Exception e) {
      log.warn("채팅 메시지 MongoDB 저장 실패 - roomId={}, action={}", res.roomId(), res.action(), e);
    }
  }

  public List<ChatMessageResponse> getRecentMessages(String roomId, int limit) {
    List<ChatMessage> messages =
        chatMessagePort.findRecentByRoomId(roomId, Math.min(Math.max(1, limit), MAX_LIMIT));
    Collections.reverse(messages);
    return messages.stream().map(ChatMessageResponse::from).toList();
  }
}
