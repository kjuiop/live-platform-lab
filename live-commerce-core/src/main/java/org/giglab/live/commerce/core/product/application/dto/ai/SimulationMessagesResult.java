package org.giglab.live.commerce.core.product.application.dto.ai;

import java.util.List;

public record SimulationMessagesResult(List<String> chatMessages, List<String> faqQuestions) {

  public static SimulationMessagesResult empty() {
    return new SimulationMessagesResult(List.of(), List.of());
  }

  public boolean isEmpty() {
    return chatMessages.isEmpty() && faqQuestions.isEmpty();
  }
}
