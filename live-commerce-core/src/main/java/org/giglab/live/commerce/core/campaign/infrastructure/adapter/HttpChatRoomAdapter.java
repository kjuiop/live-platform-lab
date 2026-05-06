package org.giglab.live.commerce.core.campaign.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.commerce.core.campaign.application.port.external.ChatRoomCreatePort;
import org.giglab.live.commerce.core.campaign.application.port.external.ChatRoomDeletePort;
import org.giglab.live.commerce.core.campaign.infrastructure.client.RestChatServerClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpChatRoomAdapter implements ChatRoomCreatePort, ChatRoomDeletePort {

  private final RestChatServerClient restChatServerClient;

  @Override
  public String createRoom(String title) {
    return restChatServerClient.createRoom(title);
  }

  @Async
  @Override
  public void deleteRoom(String roomId) {
    try {
      restChatServerClient.deleteRoom(roomId);
    } catch (Exception e) {
      log.warn("채팅방 삭제 실패 (chat-server) - roomId={}", roomId, e);
    }
  }
}
