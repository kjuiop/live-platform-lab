package org.giglab.live.commerce.core.product.infrastructure.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.global.jpa.entity.types.YnType;
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

  @Override
  public Optional<Product> findEntityById(Long productId) {
    return productRepository.findByIdAndDeleteYn(productId, YnType.N);
  }
}
