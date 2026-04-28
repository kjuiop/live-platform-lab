package org.giglab.live.commerce.core.product.application.port.persistence;

import java.util.List;
import java.util.Optional;
import org.giglab.live.commerce.core.product.domain.entity.ProductDocument;

public interface ProductDocumentStorePort {
  ProductDocument store(ProductDocument newProductDocument);

  Optional<ProductDocument> findEntityById(Long documentId);

  List<ProductDocument> findAllByProductId(Long productId);

  void markAllEmbedded(List<Long> ids);
}
