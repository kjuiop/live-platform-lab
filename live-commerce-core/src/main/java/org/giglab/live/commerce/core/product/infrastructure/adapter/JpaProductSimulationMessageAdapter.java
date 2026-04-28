package org.giglab.live.commerce.core.product.infrastructure.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductSimulationMessagePort;
import org.giglab.live.commerce.core.product.domain.entity.ProductSimulationMessage;
import org.giglab.live.commerce.core.product.infrastructure.persistence.ProductSimulationMessageRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaProductSimulationMessageAdapter implements ProductSimulationMessagePort {

  private final ProductSimulationMessageRepository repository;

  @Override
  public List<ProductSimulationMessage> saveAll(List<ProductSimulationMessage> messages) {
    return repository.saveAll(messages);
  }

  @Override
  public List<ProductSimulationMessage> findByProductId(Long productId) {
    return repository.findByProductId(productId);
  }
}
