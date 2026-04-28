package org.giglab.live.domain.model;

import java.time.Instant;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
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

  public static ChatMessage create(
      String roomId,
      String action,
      String senderUserId,
      String senderNickname,
      Map<String, Object> payload,
      Instant sentAt) {
    return ChatMessage.builder()
        .roomId(roomId)
        .action(action)
        .senderUserId(senderUserId)
        .senderNickname(senderNickname)
        .payload(payload)
        .sentAt(sentAt)
        .build();
  }
}
