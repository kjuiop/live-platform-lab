package org.giglab.live.commerce.api.mapper.campaign;

import org.giglab.live.commerce.api.dto.campaign.CampaignProductRequest;
import org.giglab.live.commerce.api.dto.campaign.CreateCampaignRequest;
import org.giglab.live.commerce.api.dto.campaign.CreateCampaignResponse;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignProductDto;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignCommand;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CampaignMapper {

  CreateCampaignCommand toCreateCampaignCommand(CreateCampaignRequest request);

  CampaignProductDto toCampaignProductDto(CampaignProductRequest request);

  CreateCampaignResponse toCreateCampaignResponse(CreateCampaignResult result);
}
