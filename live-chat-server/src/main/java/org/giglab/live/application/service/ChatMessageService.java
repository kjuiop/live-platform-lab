package org.giglab.live.application.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.application.dto.ChatMessageResponse;
import org.giglab.live.application.dto.action.ActionResponse;
import org.giglab.live.application.dto.action.Actor;
import org.giglab.live.application.port.persistence.ChatMessagePort;
import org.giglab.live.application.port.persistence.RoomSeqPort;
import org.giglab.live.domain.model.ChatMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {

  private static final int MAX_LIMIT = 200;
  private static final int MAX_RECOVERY_COUNT = 10;

  private static final Set<String> SAVEABLE_ACTIONS =
      Set.of(
          ActionType.CHAT_MESSAGE.getKey(),
          ActionType.FAQ_QUESTION.getKey(),
          ActionType.FAQ_ANSWER.getKey(),
          ActionType.FAQ_ERROR.getKey());

  private final ChatMessagePort chatMessagePort;
  private final RoomSeqPort roomSeqPort;

  public static boolean isSaveable(String action) {
    return SAVEABLE_ACTIONS.contains(action);
  }

  public ActionResponse assignSeq(ActionResponse res) {
    if (!isSaveable(res.action())) {
      return res;
    }
    return res.withSeq(roomSeqPort.nextSeq(res.roomId()));
  }

  @Async("chatAsyncExecutor")
  public void saveIfNeeded(ActionResponse res) {
    if (!isSaveable(res.action())) {
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
              res.sentAt(),
              res.seq());

      chatMessagePort.save(message);
    } catch (Exception e) {
      log.warn("채팅 메시지 MongoDB 저장 실패 - roomId={}, action={}", res.roomId(), res.action(), e);
    }
  }

  public List<ActionResponse> getRecoveryMessages(String roomId, long lastSeq, long seq) {
    long gapSize = seq - lastSeq - 1;
    if (gapSize > MAX_RECOVERY_COUNT) {
      log.warn(
          "복구 건수 초과로 스킵 - roomId={}, gapSize={}, limit={}", roomId, gapSize, MAX_RECOVERY_COUNT);
      return List.of();
    }

    List<ChatMessage> missed = chatMessagePort.findByRoomIdAndSeqBetween(roomId, lastSeq, seq);
    if (missed.isEmpty()) {
      log.warn("복구 대상 없음 - roomId={}, lastSeq={}, seq={}", roomId, lastSeq, seq);
      return List.of();
    }

    return missed.stream().map(this::toActionResponse).toList();
  }

  private ActionResponse toActionResponse(ChatMessage msg) {
    Actor actor =
        msg.getSenderUserId() != null
            ? new Actor(msg.getSenderUserId(), msg.getSenderNickname(), msg.getSenderNickname())
            : null;
    return new ActionResponse(
        msg.getRoomId(), msg.getAction(), actor, msg.getPayload(), msg.getSentAt(), msg.getSeq());
  }

  public List<ChatMessageResponse> getRecentMessages(String roomId, int limit) {
    List<ChatMessage> messages =
        chatMessagePort.findRecentByRoomId(roomId, Math.min(Math.max(1, limit), MAX_LIMIT));
    Collections.reverse(messages);
    return messages.stream().map(ChatMessageResponse::from).toList();
  }
}
