package org.giglab.live.commerce.core.product.application.port.persistence;

import java.util.Optional;
import org.giglab.live.commerce.core.product.domain.entity.Product;

public interface ProductStorePort {
  Product store(Product newProduct);

  Optional<Product> findEntityById(Long productId);
}
