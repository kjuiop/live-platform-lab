package org.giglab.live.commerce.core.campaign.infrastructure.persistence;

import static org.giglab.live.commerce.core.campaign.domain.entity.QCampaign.campaign;
import static org.giglab.live.commerce.core.campaign.domain.entity.QCampaignProduct.campaignProduct;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignListQuery;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignSummary;
import org.giglab.live.commerce.core.global.jpa.entity.types.YnType;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class CampaignQueryRepository {

  private final JPAQueryFactory queryFactory;

  public List<CampaignSummary> findList(CampaignListQuery query) {
    BooleanBuilder builder = new BooleanBuilder();
    builder.and(campaign.deleteYn.eq(YnType.N));

    if (query.cursor() != null) {
      builder.and(campaign.id.lt(query.cursor()));
    }

    if (query.status() != null) {
      builder.and(campaign.status.eq(query.status()));
    }

    if (StringUtils.hasText(query.keyword())) {
      builder.and(campaign.title.containsIgnoreCase(query.keyword()));
    }

    return queryFactory
        .select(
            Projections.constructor(
                CampaignSummary.class,
                campaign.id,
                campaign.title,
                campaign.description,
                campaign.status,
                campaign.scheduledAt,
                JPAExpressions.select(campaignProduct.count().intValue())
                    .from(campaignProduct)
                    .where(campaignProduct.campaign.id.eq(campaign.id))))
        .from(campaign)
        .where(builder)
        .orderBy(campaign.id.desc())
        .limit(query.fetchSize())
        .fetch();
  }
}
