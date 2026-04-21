package org.giglab.live.commerce.core.campaign.infrastructure.persistence;

import org.giglab.live.commerce.core.campaign.domain.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {}
