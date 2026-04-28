package org.giglab.live.commerce.core.product.application.usecase.ai;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.commerce.core.product.application.dto.ai.SimulationMessagesResult;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductSimulationMessagePort;
import org.giglab.live.commerce.core.product.domain.entity.ProductSimulationMessage;
import org.giglab.live.commerce.core.product.domain.entity.ProductSimulationMessage.MessageType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateSimulationMessagesUseCase {

  private final ProductSimulationMessagePort productSimulationMessagePort;

  @Transactional
  public SimulationMessagesResult execute(Long productId, SimulationMessagesResult result) {
    if (result.isEmpty()) {
      return result;
    }

    List<ProductSimulationMessage> entities = new ArrayList<>();
    result
        .chatMessages()
        .forEach(
            msg -> entities.add(ProductSimulationMessage.create(productId, MessageType.CHAT, msg)));
    result
        .faqQuestions()
        .forEach(q -> entities.add(ProductSimulationMessage.create(productId, MessageType.FAQ, q)));

    productSimulationMessagePort.saveAll(entities);
    log.info("[SimMsg] 저장 완료 - productId={}, total={}", productId, entities.size());
    return result;
  }
}
