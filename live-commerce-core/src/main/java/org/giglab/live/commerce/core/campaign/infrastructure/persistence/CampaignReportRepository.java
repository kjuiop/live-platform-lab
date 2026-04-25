package org.giglab.live.commerce.core.campaign.infrastructure.persistence;

import org.giglab.live.commerce.core.campaign.domain.entity.CampaignReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignReportRepository extends JpaRepository<CampaignReport, Long> {}
