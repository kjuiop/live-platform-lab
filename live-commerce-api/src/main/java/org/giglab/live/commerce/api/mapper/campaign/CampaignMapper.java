package org.giglab.live.commerce.api.mapper.campaign;

import org.giglab.live.commerce.api.dto.campaign.CampaignProductItem;
import org.giglab.live.commerce.api.dto.campaign.CampaignProductRequest;
import org.giglab.live.commerce.api.dto.campaign.CampaignSummaryItem;
import org.giglab.live.commerce.api.dto.campaign.CreateCampaignRequest;
import org.giglab.live.commerce.api.dto.campaign.CreateCampaignResponse;
import org.giglab.live.commerce.api.dto.campaign.GetCampaignListRequest;
import org.giglab.live.commerce.api.dto.campaign.GetCampaignListResponse;
import org.giglab.live.commerce.api.dto.campaign.GetCampaignResponse;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignListQuery;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignProductDto;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignSummary;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignCommand;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignResult;
import org.giglab.live.commerce.core.campaign.application.dto.GetCampaignListResult;
import org.giglab.live.commerce.core.campaign.application.dto.GetCampaignResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CampaignMapper {

  CreateCampaignCommand toCreateCampaignCommand(CreateCampaignRequest request);

  CampaignProductDto toCampaignProductDto(CampaignProductRequest request);

  CreateCampaignResponse toCreateCampaignResponse(CreateCampaignResult result);

  @Mapping(target = "size", expression = "java(request.size() != null ? request.size() : 20)")
  CampaignListQuery toCampaignListQuery(GetCampaignListRequest request);

  @Mapping(target = "status", expression = "java(summary.status().name())")
  CampaignSummaryItem toCampaignSummaryItem(CampaignSummary summary);

  GetCampaignListResponse toGetCampaignListResponse(GetCampaignListResult result);

  CampaignProductItem toCampaignProductItem(CampaignProductDto dto);

  @Mapping(target = "status", expression = "java(result.status().name())")
  GetCampaignResponse toGetCampaignResponse(GetCampaignResult result);
}
