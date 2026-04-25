package org.giglab.live.application.dto.stats;

public record ViewerStats(int totalViewers, int peakConcurrent, long avgDurationSeconds) {}
