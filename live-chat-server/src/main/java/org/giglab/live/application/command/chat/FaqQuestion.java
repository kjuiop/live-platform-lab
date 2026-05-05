package org.giglab.live.application.command.chat;

import lombok.RequiredArgsConstructor;
import org.giglab.live.application.command.AbstractActionHandler;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActionResponse;
import org.giglab.live.application.dto.action.DefaultActionResponse;
import org.giglab.live.application.service.FaqAnswerService;
import org.giglab.live.domain.exception.FaqDomainException;
import org.giglab.live.domain.exception.FaqErrorCode;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FaqQuestion extends AbstractActionHandler<ActionRequest, ActionResponse> {

  private final FaqAnswerService faqAnswerService;

  @Override
  public ActionType action() {
    return ActionType.FAQ_QUESTION;
  }

  @Override
  protected void validate(ActionRequest req) {
    requireString(
        req.payload(), "question", () -> new FaqDomainException(FaqErrorCode.EMPTY_QUESTION));
    requirePositiveLong(
        req.payload(), "productId", () -> new FaqDomainException(FaqErrorCode.INVALID_PRODUCT_ID));
  }

  @Override
  protected ActionResponse process(ActionRequest req) {
    // 1) 질문 echo — 채팅방 전체에 즉시 브로드캐스트 (controller가 처리)
    // 2) AI 호출 + FAQ.ANSWER 브로드캐스트 — 비동기
    faqAnswerService.generateAndBroadcast(req);
    return DefaultActionResponse.of(req.roomId(), req.action(), req.actor(), req.payload());
  }
}
