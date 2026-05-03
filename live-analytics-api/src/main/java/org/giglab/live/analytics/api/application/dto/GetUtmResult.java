package org.giglab.live.analytics.api.application.dto;

public record GetUtmResult(String utmSource, long visitors, long purchases, double cvrPct) {}
