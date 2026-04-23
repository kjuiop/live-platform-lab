package org.giglab.live.commerce.core.product.infrastructure.persistence;

import static org.giglab.live.commerce.core.product.domain.entity.QProductDocument.productDocument;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.dto.pdf.DocumentSummary;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductDocumentQueryRepository {

  private final JPAQueryFactory queryFactory;

  public List<DocumentSummary> getDocumentsByProductId(Long productId) {
    return this.queryFactory
        .select(
            Projections.constructor(
                DocumentSummary.class,
                productDocument.id,
                productDocument.productId,
                productDocument.filename,
                productDocument.embedYn))
        .from(productDocument)
        .where(productDocument.productId.eq(productId))
        .orderBy(productDocument.id.desc())
        .fetch();
  }
}
