package org.giglab.live.application.command.chat;

import lombok.RequiredArgsConstructor;
import org.giglab.live.application.command.ActionHandler;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActionResponse;
import org.giglab.live.application.service.FaqAnswerService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FaqQuestion implements ActionHandler<ActionRequest, ActionResponse> {

  private final FaqAnswerService faqAnswerService;

  @Override
  public ActionType action() {
    return ActionType.FAQ_QUESTION;
  }

  @Override
  public ActionResponse execute(ActionRequest req) {
    Object question = req.payload() == null ? null : req.payload().get("question");
    if (!(question instanceof String s) || s.isBlank()) {
      throw new IllegalArgumentException("payload.question is required");
    }
    Object productId = req.payload().get("productId");
    if (!(productId instanceof Number n) || n.longValue() <= 0) {
      throw new IllegalArgumentException("payload.productId must be a positive number");
    }

    // 1) 질문 echo — 채팅방 전체에 즉시 브로드캐스트 (controller가 처리)
    // 2) AI 호출 + FAQ.ANSWER 브로드캐스트 — 비동기
    faqAnswerService.generateAndBroadcast(req);

    return ActionResponse.of(req.roomId(), req.action(), req.actor(), req.payload());
  }
}
