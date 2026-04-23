package org.giglab.live.commerce.core.product.infrastructure.persistence;

import java.util.List;
import org.giglab.live.commerce.core.product.domain.entity.ProductDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductDocumentRepository extends JpaRepository<ProductDocument, Long> {
  List<ProductDocument> findAllByProductId(Long productId);

  @Modifying
  @Query("UPDATE ProductDocument d SET d.embedYn = 'Y' WHERE d.id IN :ids")
  void markAllEmbedded(@Param("ids") List<Long> ids);
}
