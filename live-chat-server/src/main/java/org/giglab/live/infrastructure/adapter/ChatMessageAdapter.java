package org.giglab.live.infrastructure.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.application.port.persistence.ChatMessagePort;
import org.giglab.live.domain.model.ChatMessage;
import org.giglab.live.infrastructure.mongo.MongoChatMessageRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessageAdapter implements ChatMessagePort {

  private final MongoChatMessageRepository mongoChatMessageRepository;

  @Override
  public void save(ChatMessage message) {
    mongoChatMessageRepository.save(message);
  }

  @Override
  public List<ChatMessage> findRecentByRoomId(String roomId, int limit) {
    return mongoChatMessageRepository.findRecentByRoomId(roomId, limit);
  }

  @Override
  public List<ChatMessage> findByRoomIdAndSeqBetween(String roomId, long fromSeq, long toSeq) {
    return mongoChatMessageRepository.findByRoomIdAndSeqBetween(roomId, fromSeq, toSeq);
  }
}
