package org.giglab.live.commerce.core.product.infrastructure.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.port.ai.SearchDocumentPort;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ElasticsearchSearchDocumentAdapter implements SearchDocumentPort {

  private final VectorStore vectorStore;

  @Override
  public List<Document> search(Long productId, String query, int topK) {
    return vectorStore.similaritySearch(
        SearchRequest.builder()
            .query(query)
            .topK(topK)
            .filterExpression("productId == " + productId)
            .build());
  }
}
