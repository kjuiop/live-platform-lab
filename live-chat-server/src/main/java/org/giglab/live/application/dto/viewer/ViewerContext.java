package org.giglab.live.application.dto.viewer;

import java.time.Instant;

public record ViewerContext(String sessionId, String roomId, String userId, Instant joinAt) {}
