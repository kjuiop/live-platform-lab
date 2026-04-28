package org.giglab.live.infrastructure.redis.pubsub;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.presentation.StompDestination;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomBroadcastSubscriber implements MessageListener {

  private final SimpMessagingTemplate messagingTemplate;
  private final ObjectMapper objectMapper;

  @Override
  public void onMessage(Message message, byte[] pattern) {
    String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
    String body = new String(message.getBody(), StandardCharsets.UTF_8);
    String roomId = RedisPubSubChannel.extractRoomId(channel);

    try {
      JsonNode payload = objectMapper.readTree(body);
      messagingTemplate.convertAndSend(StompDestination.ROOM_PREFIX + roomId, payload);
      log.debug("STOMP broadcast - roomId={}", roomId);
    } catch (Exception e) {
      log.error("Redis 수신 메시지 처리 실패 - channel={}", channel, e);
    }
  }
}
