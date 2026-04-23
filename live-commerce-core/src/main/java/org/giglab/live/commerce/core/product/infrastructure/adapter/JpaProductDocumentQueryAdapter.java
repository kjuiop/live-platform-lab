package org.giglab.live.commerce.core.product.infrastructure.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.dto.pdf.DocumentSummary;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductDocumentQueryPort;
import org.giglab.live.commerce.core.product.infrastructure.persistence.ProductDocumentQueryRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaProductDocumentQueryAdapter implements ProductDocumentQueryPort {

  private final ProductDocumentQueryRepository queryRepository;

  @Override
  public List<DocumentSummary> getDocumentsByProductId(Long productId) {
    return queryRepository.getDocumentsByProductId(productId);
  }
}
