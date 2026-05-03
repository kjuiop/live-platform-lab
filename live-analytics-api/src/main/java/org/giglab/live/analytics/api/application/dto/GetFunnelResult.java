package org.giglab.live.analytics.api.application.dto;

public record GetFunnelResult(
    // 노출 횟수
    long impression,
    // 클릭 횟수
    long click,
    // 장바구니에 담긴 횟수
    long addCart,
    // 구매 횟수
    long purchase,
    // 전환율 (구매 횟수 / 노출 횟수 * 100)
    double cvrPct) {}
