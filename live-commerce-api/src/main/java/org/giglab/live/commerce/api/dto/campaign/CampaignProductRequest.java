package org.giglab.live.commerce.api.dto.campaign;

import jakarta.validation.constraints.NotNull;

public record CampaignProductRequest(@NotNull Long productId, String name, int displayOrder) {}
