package org.giglab.live.domain.repository;

import java.util.List;
import org.giglab.live.domain.model.ChatMessage;

public interface ChatMessageRepository {

  void save(ChatMessage message);

  List<ChatMessage> findRecentByRoomId(String roomId, int limit);
}
