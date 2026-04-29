package org.giglab.live.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.application.port.persistence.RoomSeqPort;
import org.giglab.live.domain.exception.RoomDomainException;
import org.giglab.live.domain.exception.RoomErrorCode;
import org.giglab.live.infrastructure.redis.RoomSeqRepository;
import org.giglab.live.infrastructure.redis.exception.RedisException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomSeqAdapter implements RoomSeqPort {

  private final RoomSeqRepository roomSeqRepository;

  @Override
  public long nextSeq(String roomId) {
    try {
      return roomSeqRepository.nextSeq(roomId);
    } catch (RedisException e) {
      log.error("Redis seq 발급 실패: {}", e.getMessage());
      throw new RoomDomainException(RoomErrorCode.SEQ_FAILURE);
    }
  }
}
