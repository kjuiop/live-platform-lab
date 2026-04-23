package org.giglab.live.commerce.core.product.infrastructure.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductDocumentStorePort;
import org.giglab.live.commerce.core.product.domain.entity.ProductDocument;
import org.giglab.live.commerce.core.product.infrastructure.persistence.ProductDocumentRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaProductDocumentStoreAdapter implements ProductDocumentStorePort {

  private final ProductDocumentRepository productDocumentRepository;

  @Override
  public ProductDocument store(ProductDocument newProductDocument) {
    return productDocumentRepository.save(newProductDocument);
  }

  @Override
  public Optional<ProductDocument> findEntityById(Long documentId) {
    return productDocumentRepository.findById(documentId);
  }

  @Override
  public List<ProductDocument> findAllByProductId(Long productId) {
    return productDocumentRepository.findAllByProductId(productId);
  }

  @Override
  public void markAllEmbedded(List<Long> ids) {
    productDocumentRepository.markAllEmbedded(ids);
  }
}
