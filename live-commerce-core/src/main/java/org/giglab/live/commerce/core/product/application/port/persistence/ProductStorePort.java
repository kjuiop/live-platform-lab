package org.giglab.live.commerce.core.product.application.port.persistence;

import org.giglab.live.commerce.core.product.domain.entity.Product;

public interface ProductStorePort {
  Product store(Product newProduct);
}
