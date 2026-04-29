package org.giglab.live.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RoomSeqRepository {

  private static final String SEQ_KEY = "LIVE:ROOM:%s:SEQ";

  private final RedisTemplate<String, Object> redisTemplate;

  public long nextSeq(String roomId) {
    Long seq = redisTemplate.opsForValue().increment(seqKey(roomId));
    if (seq == null) {
      log.error("Redis INCR returned null - roomId={}", roomId);
      throw new IllegalStateException("Redis seq 발급 실패 - roomId: " + roomId);
    }
    return seq;
  }

  private String seqKey(String roomId) {
    return String.format(SEQ_KEY, roomId);
  }
}
