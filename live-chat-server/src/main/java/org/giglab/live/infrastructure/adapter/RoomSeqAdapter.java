package org.giglab.live.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.application.port.persistence.RoomSeqPort;
import org.giglab.live.domain.exception.RoomDomainException;
import org.giglab.live.domain.exception.RoomErrorCode;
import org.giglab.live.infrastructure.redis.RoomSeqRepository;
import org.giglab.live.infrastructure.redis.exception.RedisException;
import org.springframework.dao.DataAccessException;
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
      log.error("Redis seq 발급 실패 - roomId={}", roomId, e);
      throw new RoomDomainException(RoomErrorCode.SEQ_FAILURE, e);
    } catch (DataAccessException e) {
      log.error("Redis 연결/접근 오류 - roomId={}", roomId, e);
      throw new RoomDomainException(RoomErrorCode.SEQ_FAILURE, e);
    }
  }
}
