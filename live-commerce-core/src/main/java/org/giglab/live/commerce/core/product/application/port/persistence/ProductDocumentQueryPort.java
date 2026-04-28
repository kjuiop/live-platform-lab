package org.giglab.live.commerce.core.product.application.port.persistence;

import java.util.List;
import org.giglab.live.commerce.core.product.application.dto.pdf.DocumentSummary;

public interface ProductDocumentQueryPort {

  List<DocumentSummary> getDocumentsByProductId(Long productId);
}
