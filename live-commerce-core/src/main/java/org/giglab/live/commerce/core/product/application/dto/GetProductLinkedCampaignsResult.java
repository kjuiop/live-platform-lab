package org.giglab.live.commerce.core.product.application.dto;

import java.util.List;
import org.giglab.live.commerce.core.shared.ProductLinkedCampaignDto;

public record GetProductLinkedCampaignsResult(List<ProductLinkedCampaignDto> campaigns) {}
