package org.giglab.live.commerce.core.campaign.infrastructure.persistence;

import static org.giglab.live.commerce.core.campaign.domain.entity.QCampaign.campaign;
import static org.giglab.live.commerce.core.campaign.domain.entity.QCampaignProduct.campaignProduct;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignListQuery;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignProductDto;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignSummary;
import org.giglab.live.commerce.core.campaign.application.dto.GetCampaignResult;
import org.giglab.live.commerce.core.global.jpa.entity.types.YnType;
import org.giglab.live.commerce.core.shared.ProductLinkedCampaignDto;
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
                campaignProduct.count().intValue()))
        .from(campaign)
        .leftJoin(campaignProduct)
        .on(campaignProduct.campaign.id.eq(campaign.id))
        .where(builder)
        .groupBy(campaign.id)
        .orderBy(campaign.id.desc())
        .limit(query.fetchSize())
        .fetch();
  }

  public Optional<GetCampaignResult> findById(Long campaignId) {
    var row =
        queryFactory
            .select(
                campaign.id,
                campaign.title,
                campaign.description,
                campaign.status,
                campaign.scheduledAt,
                campaign.startedAt,
                campaign.endedAt)
            .from(campaign)
            .where(defaultCondition(), eqCampaignId(campaignId))
            .fetchOne();

    if (row == null) {
      return Optional.empty();
    }

    List<CampaignProductDto> products =
        queryFactory
            .select(
                Projections.constructor(
                    CampaignProductDto.class,
                    campaignProduct.productId,
                    campaignProduct.name,
                    campaignProduct.displayOrder))
            .from(campaignProduct)
            .where(campaignProduct.campaign.id.eq(campaignId))
            .orderBy(campaignProduct.displayOrder.asc())
            .fetch();

    return Optional.of(
        new GetCampaignResult(
            row.get(campaign.id),
            row.get(campaign.title),
            row.get(campaign.description),
            row.get(campaign.status),
            row.get(campaign.scheduledAt),
            row.get(campaign.startedAt),
            row.get(campaign.endedAt),
            products));
  }

  public List<ProductLinkedCampaignDto> findByProductId(Long productId) {
    return queryFactory
        .select(
            Projections.constructor(
                ProductLinkedCampaignDto.class,
                campaign.id,
                campaign.title,
                campaign.status,
                campaign.scheduledAt,
                campaign.startedAt,
                campaign.endedAt))
        .from(campaignProduct)
        .join(campaign)
        .on(campaign.id.eq(campaignProduct.campaign.id))
        .where(campaignProduct.productId.eq(productId), defaultCondition())
        .orderBy(campaign.scheduledAt.desc())
        .fetch();
  }

  private BooleanExpression defaultCondition() {
    return campaign.deleteYn.eq(YnType.N);
  }

  private BooleanExpression eqCampaignId(Long campaignId) {
    return campaign.id.eq(campaignId);
  }
}
