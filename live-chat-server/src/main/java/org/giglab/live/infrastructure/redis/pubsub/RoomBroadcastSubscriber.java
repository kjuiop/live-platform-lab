package org.giglab.live.infrastructure.redis.pubsub;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.application.dto.ChatMessageResponse;
import org.giglab.live.application.service.ChatMessageService;
import org.giglab.live.domain.model.ChatMessage;
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
  private final ChatMessageService chatMessageService;

  private static final int MAX_RECOVERY_COUNT = 10;

  private final ConcurrentHashMap<String, AtomicLong> lastSeqByRoom = new ConcurrentHashMap<>();

  @Override
  public void onMessage(Message message, byte[] pattern) {
    String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
    String body = new String(message.getBody(), StandardCharsets.UTF_8);
    String roomId = RedisPubSubChannel.extractRoomId(channel);

    try {
      JsonNode payload = objectMapper.readTree(body);
      long seq = payload.path("seq").asLong(0);

      if (seq > 0) {
        recoverIfGap(roomId, seq);
      }

      messagingTemplate.convertAndSend(StompDestination.ROOM_PREFIX + roomId, payload);
      log.debug("STOMP broadcast - roomId={}, seq={}", roomId, seq);
    } catch (Exception e) {
      log.error("Redis 수신 메시지 처리 실패 - channel={}", channel, e);
    }
  }

  private void recoverIfGap(String roomId, long seq) {
    AtomicLong ref = lastSeqByRoom.computeIfAbsent(roomId, k -> new AtomicLong(0));
    long lastSeq = ref.getAndSet(seq);

    if (lastSeq == 0) {
      log.debug("lastSeq 초기화 - roomId={}, seq={}", roomId, seq);
      return;
    }

    if (seq <= lastSeq + 1) {
      return;
    }

    long gapSize = seq - lastSeq - 1;
    log.info(
        "메시지 gap 감지 - roomId={}, lastSeq={}, receivedSeq={}, gapSize={}",
        roomId,
        lastSeq,
        seq,
        gapSize);

    if (gapSize > MAX_RECOVERY_COUNT) {
      log.warn(
          "복구 건수 초과로 스킵 - roomId={}, gapSize={}, limit={}", roomId, gapSize, MAX_RECOVERY_COUNT);
      return;
    }

    List<ChatMessage> missed = chatMessageService.findMissedMessages(roomId, lastSeq, seq);

    if (missed.isEmpty()) {
      log.warn("복구 대상 없음 - roomId={}, lastSeq={}, seq={}", roomId, lastSeq, seq);
      return;
    }

    log.info("누락 메시지 복구 브로드캐스트 - roomId={}, count={}", roomId, missed.size());
    for (ChatMessage msg : missed) {
      messagingTemplate.convertAndSend(
          StompDestination.ROOM_PREFIX + roomId, ChatMessageResponse.from(msg));
    }
  }
}
