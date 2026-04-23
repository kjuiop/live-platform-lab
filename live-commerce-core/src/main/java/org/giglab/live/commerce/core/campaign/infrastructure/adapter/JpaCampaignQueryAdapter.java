package org.giglab.live.commerce.core.campaign.infrastructure.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignListQuery;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignSummary;
import org.giglab.live.commerce.core.campaign.application.dto.GetCampaignResult;
import org.giglab.live.commerce.core.campaign.application.port.persistence.CampaignQueryPort;
import org.giglab.live.commerce.core.campaign.infrastructure.persistence.CampaignQueryRepository;
import org.giglab.live.commerce.core.shared.ProductLinkedCampaignDto;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaCampaignQueryAdapter implements CampaignQueryPort {

  private final CampaignQueryRepository queryRepository;

  @Override
  public List<CampaignSummary> findList(CampaignListQuery query) {
    return queryRepository.findList(query);
  }

  @Override
  public Optional<GetCampaignResult> findById(Long id) {
    return queryRepository.findById(id);
  }

  @Override
  public List<ProductLinkedCampaignDto> findByProductId(Long productId) {
    return queryRepository.findByProductId(productId);
  }
}
