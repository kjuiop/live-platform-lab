package org.giglab.live.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.giglab.live.application.port.persistence.RoomSeqPort;
import org.giglab.live.infrastructure.redis.RoomSeqRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomSeqAdapter implements RoomSeqPort {

  private final RoomSeqRepository roomSeqRepository;

  @Override
  public long nextSeq(String roomId) {
    return roomSeqRepository.nextSeq(roomId);
  }
}
