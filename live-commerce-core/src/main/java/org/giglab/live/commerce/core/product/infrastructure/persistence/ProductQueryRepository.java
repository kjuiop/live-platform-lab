package org.giglab.live.commerce.core.product.infrastructure.persistence;

import static org.giglab.live.commerce.core.product.domain.entity.QProduct.product;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.global.jpa.entity.types.YnType;
import org.giglab.live.commerce.core.product.application.dto.ProductListQuery;
import org.giglab.live.commerce.core.product.application.dto.ProductSummary;
import org.giglab.live.commerce.core.product.domain.entity.Product;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepository {

  private final JPAQueryFactory queryFactory;

  public List<ProductSummary> findList(ProductListQuery query) {
    BooleanBuilder builder = new BooleanBuilder();
    builder.and(product.deleteYn.eq(YnType.N));

    if (query.cursor() != null) {
      builder.and(product.id.gt(query.cursor()));
    }
    if (query.status() != null) {
      builder.and(product.status.eq(query.status()));
    }
    if (StringUtils.hasText(query.keyword())) {
      builder.and(product.name.containsIgnoreCase(query.keyword()));
    }

    return queryFactory
        .select(
            Projections.constructor(
                ProductSummary.class,
                product.id,
                product.name,
                product.status,
                product.price,
                product.stockQuantity))
        .from(product)
        .where(builder)
        .orderBy(product.id.asc())
        .limit(query.fetchSize())
        .fetch();
  }

  public Optional<Product> findById(Long id) {
    return Optional.ofNullable(
        queryFactory
            .selectFrom(product)
            .where(product.id.eq(id), product.deleteYn.eq(YnType.N))
            .fetchOne());
  }
}
