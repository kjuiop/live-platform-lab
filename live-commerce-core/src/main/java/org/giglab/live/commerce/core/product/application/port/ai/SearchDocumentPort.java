package org.giglab.live.commerce.core.product.application.port.ai;

import java.util.List;
import org.springframework.ai.document.Document;

public interface SearchDocumentPort {
  List<Document> search(Long productId, String query, int topK);
}
