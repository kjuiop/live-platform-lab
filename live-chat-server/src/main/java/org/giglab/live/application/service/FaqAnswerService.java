package org.giglab.live.application.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActionResponse;
import org.giglab.live.application.dto.action.Actor;
import org.giglab.live.application.dto.action.DefaultActionResponse;
import org.giglab.live.application.port.external.FaqAnswerPort;
import org.giglab.live.application.port.messaging.BroadcastPort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FaqAnswerService {

  private static final Actor AI_ACTOR = new Actor("ai", "AI 어시스턴트", "AI 어시스턴트");

  private final FaqAnswerPort faqAnswerPort;
  private final BroadcastPort broadcastPort;
  private final ChatMessageService chatMessageService;

  @Async("faqAsyncExecutor")
  public void generateAndBroadcast(ActionRequest req) {
    String question = (String) req.payload().get("question");
    Long productId = ((Number) req.payload().get("productId")).longValue();
    try {
      String rawAnswer = faqAnswerPort.ask(productId, question);
      String answer = (rawAnswer != null && !rawAnswer.isBlank()) ? rawAnswer : "답변을 생성할 수 없습니다.";
      ActionResponse res =
          DefaultActionResponse.of(
              req.roomId(),
              ActionType.FAQ_ANSWER.getKey(),
              AI_ACTOR,
              Map.of("answer", answer, "question", question));
      ActionResponse resWithSeq = chatMessageService.assignSeq(res);
      broadcastPort.publish(req.roomId(), resWithSeq);
      chatMessageService.saveIfNeeded(resWithSeq);
      log.info("FAQ 답변 브로드캐스트 - roomId={}, productId={}", req.roomId(), productId);
    } catch (Exception e) {
      log.warn("FAQ 답변 생성 실패 - roomId={}", req.roomId(), e);
      ActionResponse errRes =
          DefaultActionResponse.of(
              req.roomId(),
              ActionType.FAQ_ERROR.getKey(),
              AI_ACTOR,
              Map.of("message", "답변 생성에 실패했습니다. 잠시 후 다시 시도해주세요.", "question", question));
      ActionResponse errResWithSeq = chatMessageService.assignSeq(errRes);
      broadcastPort.publish(req.roomId(), errResWithSeq);
      chatMessageService.saveIfNeeded(errResWithSeq);
    }
  }
}
