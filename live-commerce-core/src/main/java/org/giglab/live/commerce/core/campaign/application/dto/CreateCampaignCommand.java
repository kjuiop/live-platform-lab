package org.giglab.live.commerce.core.campaign.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public record CreateCampaignCommand(
    @NotBlank String title,
    @NotBlank String description,
    @NotNull LocalDateTime scheduledAt,
    @Size(min = 1) @NotNull List<CampaignProductDto> campaignProducts) {}
