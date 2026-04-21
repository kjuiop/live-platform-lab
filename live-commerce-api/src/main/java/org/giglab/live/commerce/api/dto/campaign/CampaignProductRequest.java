package org.giglab.live.commerce.api.dto.campaign;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CampaignProductRequest(
    @NotNull Long productId, @NotBlank String name, int displayOrder) {}
