package org.giglab.live.commerce.core.product.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductStorePort;
import org.giglab.live.commerce.core.product.domain.entity.Product;
import org.giglab.live.commerce.core.product.infrastructure.persistence.ProductRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaProductStoreAdapter implements ProductStorePort {

  private final ProductRepository productRepository;

  @Override
  public Product store(Product newProduct) {
    return productRepository.save(newProduct);
  }
}
