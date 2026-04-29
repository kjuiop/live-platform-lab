package org.giglab.live.application.port.persistence;

public interface RoomSeqPort {

  long nextSeq(String roomId);
}
