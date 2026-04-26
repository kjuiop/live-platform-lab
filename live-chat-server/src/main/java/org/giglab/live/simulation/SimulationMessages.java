package org.giglab.live.simulation;

import java.util.List;

public record SimulationMessages(List<String> chatMessages, List<String> faqQuestions) {

  public boolean isEmpty() {
    return chatMessages.isEmpty() && faqQuestions.isEmpty();
  }
}
