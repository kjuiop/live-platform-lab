package org.giglab.live.commerce.core.product.application.usecase.ai;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.dto.ai.SimulationMessagesResult;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductSimulationMessagePort;
import org.giglab.live.commerce.core.product.domain.entity.ProductSimulationMessage;
import org.giglab.live.commerce.core.product.domain.entity.ProductSimulationMessage.MessageType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetSimulationMessagesUseCase {

  private final ProductSimulationMessagePort productSimulationMessagePort;

  public SimulationMessagesResult execute(Long productId) {
    List<ProductSimulationMessage> all = productSimulationMessagePort.findByProductId(productId);

    List<String> chatMessages =
        all.stream()
            .filter(m -> m.getMessageType() == MessageType.CHAT)
            .map(ProductSimulationMessage::getContent)
            .toList();

    List<String> faqQuestions =
        all.stream()
            .filter(m -> m.getMessageType() == MessageType.FAQ)
            .map(ProductSimulationMessage::getContent)
            .toList();

    return new SimulationMessagesResult(chatMessages, faqQuestions);
  }
}
