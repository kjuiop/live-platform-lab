package org.giglab.live.commerce.core.campaign.infrastructure.persistence;

import java.util.Optional;
import org.giglab.live.commerce.core.campaign.domain.entity.Campaign;
import org.giglab.live.commerce.core.global.jpa.entity.types.YnType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
  Optional<Campaign> findByIdAndDeleteYn(Long id, YnType deleteYn);
}
