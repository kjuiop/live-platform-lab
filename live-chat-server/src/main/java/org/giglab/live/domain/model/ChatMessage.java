package org.giglab.live.domain.model;

import java.time.Instant;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import org.giglab.live.application.dto.action.ActionResponse;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@Document(collection = "chat_messages")
@CompoundIndex(def = "{'roomId': 1, 'sentAt': -1}")
public class ChatMessage {

  @Id private String id;

  private String roomId;
  private String action;
  private String senderUserId;
  private String senderNickname;
  private Map<String, Object> payload;
  private Instant sentAt;

  public static ChatMessage from(ActionResponse res) {
    return ChatMessage.builder()
        .roomId(res.roomId())
        .action(res.action())
        .senderUserId(res.actor() != null ? res.actor().userId() : null)
        .senderNickname(res.actor() != null ? res.actor().sender() : null)
        .payload(res.payload())
        .sentAt(res.sentAt())
        .build();
  }
}
