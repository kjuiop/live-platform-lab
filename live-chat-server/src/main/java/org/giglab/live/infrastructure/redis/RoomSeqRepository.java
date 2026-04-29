package org.giglab.live.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RoomSeqRepository {

  private static final String SEQ_KEY = "LIVE:ROOM:%s:SEQ";

  private final RedisTemplate<String, Object> redisTemplate;

  public long nextSeq(String roomId) {
    Long seq = redisTemplate.opsForValue().increment(seqKey(roomId));
    return seq != null ? seq : 1L;
  }

  private String seqKey(String roomId) {
    return String.format(SEQ_KEY, roomId);
  }
}
