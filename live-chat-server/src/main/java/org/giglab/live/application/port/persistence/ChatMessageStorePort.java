package org.giglab.live.application.port.persistence;

import org.giglab.live.domain.model.ChatMessage;

public interface ChatMessageStorePort {

  void save(ChatMessage chatMessage);
}
