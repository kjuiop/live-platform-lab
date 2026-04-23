package org.giglab.live.commerce.core.product.application.usecase.ai;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.dto.pdf.DocumentSummary;
import org.giglab.live.commerce.core.product.application.dto.pdf.GetDocumentListResult;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductDocumentQueryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetDocumentListUseCase {

  private final ProductDocumentQueryPort productDocumentQueryPort;

  public GetDocumentListResult execute(Long productId) {
    List<DocumentSummary> documents = productDocumentQueryPort.getDocumentsByProductId(productId);
    return new GetDocumentListResult(documents);
  }
}
