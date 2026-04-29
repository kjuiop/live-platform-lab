package org.giglab.live.application.port.persistence;

import java.util.List;
import org.giglab.live.domain.model.ChatMessage;

public interface ChatMessagePort {

  List<ChatMessage> findRecentByRoomId(String roomId, int limit);

  void save(ChatMessage chatMessage);

  List<ChatMessage> findByRoomIdAndSeqBetween(String roomId, long fromSeq, long toSeq);
}
