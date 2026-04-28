package org.giglab.live.commerce.core.product.infrastructure.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.port.ai.EmbedDocumentPort;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ElasticsearchEmbedDocumentAdapter implements EmbedDocumentPort {

  private final VectorStore vectorStore;

  @Override
  public void embed(List<Document> chunks) {
    vectorStore.add(chunks);
  }
}
