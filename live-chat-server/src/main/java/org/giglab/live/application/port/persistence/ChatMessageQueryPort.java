package org.giglab.live.application.port.persistence;

import java.util.List;
import org.giglab.live.domain.model.ChatMessage;

public interface ChatMessageQueryPort {

  List<ChatMessage> findRecentByRoomId(String roomId, int limit);
}
