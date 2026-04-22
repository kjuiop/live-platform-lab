package org.giglab.live.commerce.core.product.application.usecase.ai;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.dto.ai.EmbedAllDocumentsContext;
import org.giglab.live.commerce.core.product.application.dto.ai.EmbedAllDocumentsResult;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductDocumentStorePort;
import org.giglab.live.commerce.core.product.domain.entity.ProductDocument;
import org.giglab.live.commerce.core.product.domain.exception.ProductDomainException;
import org.giglab.live.commerce.core.product.domain.exception.ProductErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MarkAllDocumentsEmbeddedUseCase {

  private final ProductDocumentStorePort productDocumentStorePort;

  public EmbedAllDocumentsResult execute(EmbedAllDocumentsContext context) {
    for (Long docId : context.embeddedDocIds()) {

      Optional<ProductDocument> findDocument = productDocumentStorePort.findEntityById(docId);
      if (findDocument.isEmpty()) {
        throw new ProductDomainException(
            ProductErrorCode.PDF_NOT_FOUND,
            String.format("PDF 문서를 찾을 수 없습니다. documentId=%d", docId));
      }
      ProductDocument document = findDocument.get();
      document.markAsEmbedded();
    }

    return new EmbedAllDocumentsResult(context.totalCount(), context.embeddedDocIds().size());
  }
}
