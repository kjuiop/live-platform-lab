package org.giglab.live.application.port.persistence;

import java.time.Instant;

public record ViewerContext(String sessionId, String roomId, String userId, Instant joinAt) {}
