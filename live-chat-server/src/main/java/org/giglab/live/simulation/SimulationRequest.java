package org.giglab.live.simulation;

public record SimulationRequest(String roomId, Long productId, int viewerCount, int messageCount) {}
