package org.giglab.live.infrastructure.mongo;

import org.giglab.live.domain.model.ViewerSession;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoViewerSessionRepository extends MongoRepository<ViewerSession, String> {}
