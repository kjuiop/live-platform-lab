package org.giglab.live.commerce.core.product.application.usecase.ai;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.dto.ai.EmbedAllDocumentsContext;
import org.giglab.live.commerce.core.product.application.dto.ai.EmbedAllDocumentsResult;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductDocumentStorePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MarkAllDocumentsEmbeddedUseCase {

  private final ProductDocumentStorePort productDocumentStorePort;

  public EmbedAllDocumentsResult execute(EmbedAllDocumentsContext context) {
    List<Long> docIds = context.embeddedDocIds();
    if (!docIds.isEmpty()) {
      productDocumentStorePort.markAllEmbedded(docIds);
    }
    return new EmbedAllDocumentsResult(context.totalCount(), docIds.size());
  }
}
