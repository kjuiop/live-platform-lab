package org.giglab.live.commerce.api.mapper.campaign;

import java.util.List;
import org.giglab.live.commerce.api.dto.campaign.BroadcastStatusResponse;
import org.giglab.live.commerce.api.dto.campaign.CampaignSummaryItem;
import org.giglab.live.commerce.api.dto.campaign.CreateCampaignReportRequest;
import org.giglab.live.commerce.api.dto.campaign.CreateCampaignRequest;
import org.giglab.live.commerce.api.dto.campaign.CreateCampaignResponse;
import org.giglab.live.commerce.api.dto.campaign.GetCampaignListRequest;
import org.giglab.live.commerce.api.dto.campaign.GetCampaignListResponse;
import org.giglab.live.commerce.api.dto.campaign.GetCampaignResponse;
import org.giglab.live.commerce.core.campaign.application.dto.BroadcastStatusResult;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignListQuery;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignPageQuery;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignSummary;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignCommand;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignReportCommand;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignResult;
import org.giglab.live.commerce.core.campaign.application.dto.GetCampaignListResult;
import org.giglab.live.commerce.core.campaign.application.dto.GetCampaignPageResult;
import org.giglab.live.commerce.core.campaign.application.dto.GetCampaignResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CampaignMapper {

  CreateCampaignCommand toCreateCampaignCommand(CreateCampaignRequest request);

  CreateCampaignResponse toCreateCampaignResponse(CreateCampaignResult result);

  @Mapping(target = "size", expression = "java(request.sizeOrDefault())")
  CampaignListQuery toCampaignListQuery(GetCampaignListRequest request);

  @Mapping(target = "page", expression = "java(request.pageOrDefault())")
  @Mapping(target = "size", expression = "java(request.sizeOrDefault())")
  CampaignPageQuery toCampaignPageQuery(GetCampaignListRequest request);

  @Mapping(target = "status", expression = "java(summary.status().name())")
  CampaignSummaryItem toCampaignSummaryItem(CampaignSummary summary);

  default GetCampaignListResponse toGetCampaignListResponseFromCursor(
      GetCampaignListResult result) {
    List<CampaignSummaryItem> items =
        result.items().stream().map(this::toCampaignSummaryItem).toList();
    return GetCampaignListResponse.ofCursor(items, result.nextCursor(), result.hasNext());
  }

  default GetCampaignListResponse toGetCampaignListResponseFromPage(GetCampaignPageResult result) {
    List<CampaignSummaryItem> items =
        result.items().stream().map(this::toCampaignSummaryItem).toList();
    return GetCampaignListResponse.ofOffset(
        items, result.page(), result.totalPages(), result.totalCount());
  }

  @Mapping(target = "status", expression = "java(result.status().name())")
  GetCampaignResponse toGetCampaignResponse(GetCampaignResult result);

  @Mapping(target = "status", expression = "java(result.status().name())")
  BroadcastStatusResponse toBroadcastStatusResponse(BroadcastStatusResult result);

  CreateCampaignReportCommand toSaveCampaignReportCommand(CreateCampaignReportRequest request);
}
