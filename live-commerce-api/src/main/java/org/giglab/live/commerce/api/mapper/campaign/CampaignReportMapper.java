package org.giglab.live.commerce.api.mapper.campaign;

import org.giglab.live.commerce.api.dto.broadcast.SaveCampaignReportRequest;
import org.giglab.live.commerce.core.campaign.application.dto.SaveCampaignReportCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CampaignReportMapper {

  SaveCampaignReportCommand toSaveCampaignReportCommand(SaveCampaignReportRequest request);
}
