package org.giglab.live.commerce.core.product.infrastructure.persistence;

import static org.giglab.live.commerce.core.product.domain.entity.QProduct.product;
import static org.giglab.live.commerce.core.product.domain.entity.QProductCategory.productCategory;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
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
    builder.and(defaultCondition());

    if (query.cursor() != null) {
      builder.and(product.id.lt(query.cursor()));
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
                product.stockQuantity,
                JPAExpressions.select(productCategory.categoryName)
                    .from(productCategory)
                    .where(productCategory.product.id.eq(product.id))
                    .orderBy(productCategory.sortOrder.asc())
                    .limit(1)))
        .from(product)
        .where(builder)
        .orderBy(product.id.desc())
        .limit(query.fetchSize())
        .fetch();
  }

  public Optional<Product> findById(Long id) {
    return Optional.ofNullable(
        queryFactory.selectFrom(product).where(defaultCondition(), product.id.eq(id)).fetchOne());
  }

  private BooleanExpression defaultCondition() {
    return product.deleteYn.eq(YnType.N);
  }
}
