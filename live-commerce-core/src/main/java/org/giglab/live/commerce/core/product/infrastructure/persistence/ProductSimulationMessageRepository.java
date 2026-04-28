package org.giglab.live.commerce.core.product.infrastructure.persistence;

import java.util.List;
import org.giglab.live.commerce.core.product.domain.entity.ProductSimulationMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSimulationMessageRepository
    extends JpaRepository<ProductSimulationMessage, Long> {

  List<ProductSimulationMessage> findByProductId(Long productId);
}
