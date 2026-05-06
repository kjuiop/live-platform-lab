package org.giglab.live.application.port.external;

public interface FaqAnswerPort {

  String ask(Long productId, String question);
}
