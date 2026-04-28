package org.giglab.live.domain.model;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@Document(collection = "peak_viewer_snapshots")
@CompoundIndex(def = "{'roomId': 1, 'recordedAt': -1}")
public class PeakViewerSnapshot {

  @Id private String id;
  private String roomId;
  private int viewerCount;
  private Instant recordedAt;
}
