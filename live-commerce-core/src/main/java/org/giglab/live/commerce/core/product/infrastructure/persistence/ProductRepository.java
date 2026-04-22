package org.giglab.live.commerce.core.product.infrastructure.persistence;

import java.util.Optional;
import org.giglab.live.commerce.core.global.jpa.entity.types.YnType;
import org.giglab.live.commerce.core.product.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
  Optional<Product> findByIdAndDeleteYn(Long productId, YnType deleteYn);
}
