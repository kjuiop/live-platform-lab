package org.giglab.live.commerce.api.mapper.campaign;

import org.giglab.live.commerce.api.dto.campaign.CreateCampaignReportRequest;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignReportCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CampaignReportMapper {

  CreateCampaignReportCommand toSaveCampaignReportCommand(CreateCampaignReportRequest request);
}
