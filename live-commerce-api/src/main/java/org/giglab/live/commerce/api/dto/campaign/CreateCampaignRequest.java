package org.giglab.live.commerce.api.dto.campaign;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public record CreateCampaignRequest(
    @NotBlank String title,
    @NotBlank String description,
    @NotNull LocalDateTime scheduledAt,
    @Size(min = 1) @NotNull @Valid List<CampaignProductRequest> campaignProducts) {}
