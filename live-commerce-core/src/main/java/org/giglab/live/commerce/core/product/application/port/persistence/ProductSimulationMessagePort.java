package org.giglab.live.commerce.core.product.application.port.persistence;

import java.util.List;
import org.giglab.live.commerce.core.product.domain.entity.ProductSimulationMessage;

public interface ProductSimulationMessagePort {

  List<ProductSimulationMessage> saveAll(List<ProductSimulationMessage> messages);

  List<ProductSimulationMessage> findByProductId(Long productId);
}
