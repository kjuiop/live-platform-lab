package org.giglab.live.analytics.api.application.dto;

public record GetViewerResult(
    // stream.join 기준 중복 제거된 누적 시청자 수
    long totalViewers) {}
