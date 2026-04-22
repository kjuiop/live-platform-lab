package org.giglab.live.commerce.core.product.application.port.persistence;

import org.giglab.live.commerce.core.product.domain.entity.ProductDocument;

public interface ProductDocumentStorePort {
  ProductDocument store(ProductDocument newProductDocument);
}
