package org.giglab.live.commerce.core.product.application.usecase.ai;

import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductDocumentStorePort;
import org.giglab.live.commerce.core.product.domain.entity.ProductDocument;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateProductDocumentUseCase {

  private final ProductDocumentStorePort productDocumentStorePort;

  public Long execute(String filename, String extractedText) {
    ProductDocument saved =
        productDocumentStorePort.store(ProductDocument.pending(filename, extractedText));
    return saved.getId();
  }
}
