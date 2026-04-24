package org.giglab.live.domain.model;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@Document(collection = "viewer_sessions")
@CompoundIndex(def = "{'roomId': 1, 'joinAt': -1}")
public class ViewerSession {

  @Id private String id;
  private String sessionId;
  private String roomId;
  private String userId; // nullable (CHAT_JOIN 미전송 시)
  private Instant joinAt;
  private Instant leaveAt;
  private long durationSeconds;
}
