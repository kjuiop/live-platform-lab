package org.giglab.live.infrastructure.redis.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomBroadcastSubscriber implements MessageListener {

  private static final String STOMP_PREFIX = "/sub/room/";

  private final SimpMessagingTemplate messagingTemplate;
  private final ObjectMapper objectMapper;

  @Override
  public void onMessage(Message message, byte[] pattern) {
    String channel = new String(message.getChannel());
    String body = new String(message.getBody());
    String roomId = RedisPubSubChannel.extractRoomId(channel);

    try {
      Object payload = objectMapper.readValue(body, Object.class);
      messagingTemplate.convertAndSend(STOMP_PREFIX + roomId, payload);
      log.debug("STOMP broadcast - roomId={}", roomId);
    } catch (Exception e) {
      log.error("Redis 수신 메시지 처리 실패 - channel={}", channel, e);
    }
  }
}
