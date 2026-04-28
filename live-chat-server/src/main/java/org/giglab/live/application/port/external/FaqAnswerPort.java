package org.giglab.live.application.port.external;

public interface FaqAnswerPort {

  /** AI FAQ 답변 생성. 실패 시 예외 던짐. */
  String ask(Long productId, String question);
}
