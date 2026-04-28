package org.giglab.live.infrastructure.mongo;

import org.giglab.live.domain.model.PeakViewerSnapshot;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoPeakViewerSnapshotRepository
    extends MongoRepository<PeakViewerSnapshot, String> {}
