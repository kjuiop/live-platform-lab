package org.giglab.live.infrastructure.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.application.port.persistence.BroadcastPort;
import org.giglab.live.infrastructure.redis.pubsub.RedisPubSubChannel;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomBroadcastPublisher implements BroadcastPort {

  private final RedisTemplate<String, String> stringRedisTemplate;
  private final ObjectMapper objectMapper;

  public void publish(String roomId, Object payload) {
    String channel = RedisPubSubChannel.roomChannel(roomId);
    try {
      String json = objectMapper.writeValueAsString(payload);
      stringRedisTemplate.convertAndSend(channel, json);
      log.debug("Redis publish - channel={}", channel);
    } catch (JsonProcessingException e) {
      log.error("Redis publish 직렬화 실패 - roomId={}", roomId, e);
    } catch (Exception e) {
      log.error("Redis publish 실패 - roomId={}, channel={}", roomId, channel, e);
    }
  }
}
