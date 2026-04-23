package org.giglab.live.commerce.core.product.application.dto;

import java.util.List;
import org.giglab.live.commerce.core.campaign.application.dto.ProductLinkedCampaignDto;

public record GetProductLinkedCampaignsResult(List<ProductLinkedCampaignDto> campaigns) {}
