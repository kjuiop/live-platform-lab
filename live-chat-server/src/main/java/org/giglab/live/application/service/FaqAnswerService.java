package org.giglab.live.application.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActionResponse;
import org.giglab.live.application.dto.action.Actor;
import org.giglab.live.application.port.external.FaqAnswerPort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FaqAnswerService {

  private static final String SUBSCRIBE_PREFIX = "/sub/room/";
  private static final Actor AI_ACTOR = new Actor("ai", "AI 어시스턴트", "AI 어시스턴트");

  private final FaqAnswerPort faqAnswerPort;
  private final SimpMessagingTemplate operations;

  @Async
  public void generateAndBroadcast(ActionRequest req) {
    String question = (String) req.payload().get("question");
    Long productId = ((Number) req.payload().get("productId")).longValue();

    try {
      String answer = faqAnswerPort.ask(productId, question);
      ActionResponse res =
          ActionResponse.of(
              req.roomId(),
              ActionType.FAQ_ANSWER.getKey(),
              AI_ACTOR,
              Map.of("answer", answer, "question", question));
      operations.convertAndSend(SUBSCRIBE_PREFIX + req.roomId(), res);
      log.info("FAQ 답변 브로드캐스트 - roomId={}, productId={}", req.roomId(), productId);
    } catch (Exception e) {
      log.warn(
          "FAQ 답변 생성 실패 - roomId={}, productId={}, error={}",
          req.roomId(),
          productId,
          e.getMessage());
      ActionResponse errRes =
          ActionResponse.of(
              req.roomId(),
              ActionType.FAQ_ERROR.getKey(),
              AI_ACTOR,
              Map.of("message", "답변 생성에 실패했습니다. 잠시 후 다시 시도해주세요.", "question", question));
      operations.convertAndSend(SUBSCRIBE_PREFIX + req.roomId(), errRes);
    }
  }
}
