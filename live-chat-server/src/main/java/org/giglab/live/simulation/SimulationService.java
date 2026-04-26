package org.giglab.live.simulation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActionResponse;
import org.giglab.live.application.dto.action.Actor;
import org.giglab.live.application.service.ChatMessageService;
import org.giglab.live.application.service.FaqAnswerService;
import org.giglab.live.infrastructure.client.RestCommerceClient;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("local")
@RequiredArgsConstructor
public class SimulationService {

  private static final String SUB_PREFIX = "/sub/room/";

  private final SimpMessagingTemplate operations;
  private final ChatMessageService chatMessageService;
  private final FaqAnswerService faqAnswerService;
  private final RestCommerceClient commerceClient;

  @Async("faqAsyncExecutor")
  public void run(String roomId, Long productId, int viewerCount, int messageCount) {
    log.info("[Simulation] 시작 - roomId={}, productId={}", roomId, productId);

    List<String> chatMessages;
    List<String> faqQuestions;
    try {
      SimulationMessages simMsgs = commerceClient.getSimulationMessages(productId);
      if (simMsgs != null && !simMsgs.isEmpty()) {
        chatMessages = simMsgs.chatMessages();
        faqQuestions = simMsgs.faqQuestions();
        log.info(
            "[Simulation] 저장된 메시지 사용 - chats={}, faqs={}",
            chatMessages.size(),
            faqQuestions.size());
      } else {
        chatMessages = SimulationScenario.FALLBACK_CHAT_MESSAGES;
        faqQuestions = SimulationScenario.FALLBACK_FAQ_QUESTIONS;
        log.info("[Simulation] 폴백 메시지 사용");
      }
    } catch (Exception e) {
      log.warn("[Simulation] 메시지 조회 실패, 폴백 사용: {}", e.getMessage());
      chatMessages = SimulationScenario.FALLBACK_CHAT_MESSAGES;
      faqQuestions = SimulationScenario.FALLBACK_FAQ_QUESTIONS;
    }

    List<Actor> viewers =
        SimulationScenario.VIEWERS.subList(
            0, Math.min(viewerCount, SimulationScenario.VIEWERS.size()));

    try {
      // Phase 1: 시청자 순차 입장
      for (Actor actor : viewers) {
        broadcast(roomId, ActionType.CHAT_JOIN.getKey(), actor, Map.of());
        sleep(500, 1500);
      }

      // Phase 2: 채팅 + FAQ 혼합 (4번 채팅마다 FAQ 1회 삽입)
      int faqIdx = 0;
      for (int i = 0; i < messageCount; i++) {
        Actor actor = viewers.get(i % viewers.size());
        String msg = chatMessages.get(i % chatMessages.size());

        if (i > 0 && i % 4 == 0 && faqIdx < faqQuestions.size()) {
          sendFaq(roomId, productId, actor, faqQuestions.get(faqIdx++));
          sleep(1000, 2000);
        }

        broadcast(roomId, ActionType.CHAT_MESSAGE.getKey(), actor, Map.of("message", msg));
        sleep(800, 2000);
      }

      // 남은 FAQ 질문 모두 발송
      while (faqIdx < faqQuestions.size()) {
        sendFaq(
            roomId, productId, viewers.get(faqIdx % viewers.size()), faqQuestions.get(faqIdx++));
        sleep(2000, 4000);
      }

      // Phase 3: 시청자 순차 퇴장
      for (Actor actor : viewers) {
        broadcast(roomId, ActionType.CHAT_LEAVE.getKey(), actor, Map.of());
        sleep(1000, 2000);
      }

      log.info("[Simulation] 완료 - roomId={}", roomId);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.warn("[Simulation] 중단 - roomId={}", roomId);
    }
  }

  private void sendFaq(String roomId, Long productId, Actor actor, String question) {
    ActionRequest req =
        new ActionRequest(
            roomId,
            ActionType.FAQ_QUESTION.getKey(),
            actor,
            Map.of("question", question, "productId", productId));
    ActionResponse res =
        ActionResponse.of(
            roomId,
            ActionType.FAQ_QUESTION.getKey(),
            actor,
            Map.of("question", question, "productId", productId));
    operations.convertAndSend(SUB_PREFIX + roomId, res);
    chatMessageService.saveIfNeeded(res);
    faqAnswerService.generateAndBroadcast(req);
  }

  private void broadcast(String roomId, String action, Actor actor, Map<String, Object> payload) {
    ActionResponse res = ActionResponse.of(roomId, action, actor, payload);
    operations.convertAndSend(SUB_PREFIX + roomId, res);
    chatMessageService.saveIfNeeded(res);
  }

  private void sleep(int min, int max) throws InterruptedException {
    Thread.sleep(ThreadLocalRandom.current().nextInt(min, max));
  }
}
