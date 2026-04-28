package org.giglab.live.commerce.core.product.application.port.ai;

import java.util.List;
import org.springframework.ai.document.Document;

public interface EmbedDocumentPort {
  void embed(List<Document> chunks);
}
