package org.giglab.live.commerce.core.product.infrastructure.persistence;

import static org.giglab.live.commerce.core.product.domain.entity.QProduct.product;
import static org.giglab.live.commerce.core.product.domain.entity.QProductCategory.productCategory;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.global.jpa.entity.types.YnType;
import org.giglab.live.commerce.core.product.application.dto.ProductListQuery;
import org.giglab.live.commerce.core.product.application.dto.ProductPageQuery;
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
                    .limit(1),
                product.embeddingStatus))
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

  // ── 페이지 번호 기반 조회 (커버링 인덱스 2단계 전략) ──────────────────────────

  public List<ProductSummary> findPage(ProductPageQuery query) {
    List<Long> ids = findIdsByPage(query);
    if (ids.isEmpty()) {
      return List.of();
    }

    // Query 1: 상품 기본 정보 조회 (상관 서브쿼리 없이)
    var rows =
        queryFactory
            .select(
                product.id,
                product.name,
                product.status,
                product.price,
                product.stockQuantity,
                product.embeddingStatus)
            .from(product)
            .where(product.id.in(ids))
            .orderBy(product.id.desc())
            .fetch();

    // Query 2: 대표 카테고리 일괄 조회 (sortOrder 최솟값 기준) — 상관 서브쿼리 제거
    Map<Long, String> categoryMap = new HashMap<>();
    queryFactory
        .select(productCategory.product.id, productCategory.categoryName)
        .from(productCategory)
        .where(productCategory.product.id.in(ids))
        .orderBy(productCategory.product.id.asc(), productCategory.sortOrder.asc())
        .fetch()
        .forEach(
            t -> {
              Long productId = t.get(productCategory.product.id);
              if (productId != null) {
                categoryMap.putIfAbsent(productId, t.get(productCategory.categoryName));
              }
            });

    return rows.stream()
        .map(
            row ->
                new ProductSummary(
                    row.get(product.id),
                    row.get(product.name),
                    row.get(product.status),
                    row.get(product.price),
                    row.get(product.stockQuantity),
                    categoryMap.get(row.get(product.id)),
                    row.get(product.embeddingStatus)))
        .toList();
  }

  public long countPage(ProductPageQuery query) {
    Long count =
        queryFactory.select(product.count()).from(product).where(pageCondition(query)).fetchOne();
    return count != null ? count : 0L;
  }

  private List<Long> findIdsByPage(ProductPageQuery query) {
    return queryFactory
        .select(product.id)
        .from(product)
        .where(pageCondition(query))
        .orderBy(product.id.desc())
        .offset(query.offset())
        .limit(query.size())
        .fetch();
  }

  private BooleanBuilder pageCondition(ProductPageQuery query) {
    BooleanBuilder builder = new BooleanBuilder();
    builder.and(defaultCondition());
    if (query.status() != null) {
      builder.and(product.status.eq(query.status()));
    }
    if (StringUtils.hasText(query.keyword())) {
      builder.and(product.name.containsIgnoreCase(query.keyword()));
    }
    return builder;
  }

  // ─────────────────────────────────────────────────────────────────────────────

  private BooleanExpression defaultCondition() {
    return product.deleteYn.eq(YnType.N);
  }
}
