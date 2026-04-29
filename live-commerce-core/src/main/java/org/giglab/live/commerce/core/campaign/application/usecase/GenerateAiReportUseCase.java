package org.giglab.live.commerce.core.campaign.application.usecase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignInsightResult;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignReportCommand;
import org.giglab.live.commerce.core.campaign.application.dto.GenerateAiReportCommand;
import org.giglab.live.commerce.core.campaign.application.port.persistence.CampaignStorePort;
import org.giglab.live.commerce.core.campaign.application.prompt.ChatInsightPromptBuilder;
import org.giglab.live.commerce.core.campaign.application.sampler.ChatSampleTokenBudget;
import org.giglab.live.commerce.core.campaign.domain.entity.Campaign;
import org.giglab.live.commerce.core.campaign.domain.exception.CampaignDomainException;
import org.giglab.live.commerce.core.campaign.domain.exception.CampaignErrorCode;
import org.giglab.live.commerce.core.campaign.infrastructure.ai.ChatSentimentClassifier;
import org.giglab.live.commerce.core.campaign.infrastructure.client.RestChatServerClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class GenerateAiReportUseCase {

  private final CampaignStorePort campaignStorePort;
  private final RestChatServerClient chatServerClient;
  private final ChatInsightPromptBuilder promptBuilder;
  private final CreateCampaignReportUseCase createCampaignReportUseCase;
  private final ChatSentimentClassifier sentimentClassifier;
  private final ChatClient chatClient;

  public GenerateAiReportUseCase(
      CampaignStorePort campaignStorePort,
      RestChatServerClient chatServerClient,
      ChatInsightPromptBuilder promptBuilder,
      CreateCampaignReportUseCase createCampaignReportUseCase,
      ChatSentimentClassifier sentimentClassifier,
      @Qualifier("reportChatClient") ChatClient chatClient) {
    this.campaignStorePort = campaignStorePort;
    this.chatServerClient = chatServerClient;
    this.promptBuilder = promptBuilder;
    this.createCampaignReportUseCase = createCampaignReportUseCase;
    this.sentimentClassifier = sentimentClassifier;
    this.chatClient = chatClient;
  }

  @Async
  public void execute(GenerateAiReportCommand command) {
    log.info("AI 리포트 생성 시작 - campaignId={}, roomId={}", command.campaignId(), command.roomId());
    try {
      Optional<Campaign> findCampaign = campaignStorePort.findEntityById(command.campaignId());
      if (findCampaign.isEmpty()) {
        throw new CampaignDomainException(
            CampaignErrorCode.NOT_FOUND, "campaignId=" + command.campaignId());
      }

      // 1. live-chat-server에서 raw 메시지 + 집계 데이터 조회
      CampaignInsightResult raw = chatServerClient.getInsight(command.roomId());

      // 2. LLM pre-classifier로 감성 분류
      Map<String, String> labels = sentimentClassifier.classify(raw.rawMessages());

      // 3. 레이블별 균형 샘플링
      List<String> positives = ChatSampleTokenBudget.sampleByLabel(labels, "POSITIVE");
      List<String> negatives = ChatSampleTokenBudget.sampleByLabel(labels, "NEGATIVE");

      // 4. 긍정/부정 샘플이 부족하면 NEUTRAL로 맥락 보완
      List<String> contextMessages = List.of();
      if (ChatSampleTokenBudget.needsFallback(positives, negatives)) {
        contextMessages = ChatSampleTokenBudget.sampleByLabel(labels, "NEUTRAL");
      }

      // 5. 토큰 예산 컷
      List<String> allSamples = new ArrayList<>();
      allSamples.addAll(positives);
      allSamples.addAll(negatives);
      allSamples.addAll(contextMessages);
      allSamples = ChatSampleTokenBudget.applyBudget(allSamples);

      int totalTokens = allSamples.stream().mapToInt(ChatSampleTokenBudget::estimateTokens).sum();
      log.info(
          "채팅 샘플링 완료 - positive={}, negative={}, context={}, totalTokens≈{}",
          positives.size(),
          negatives.size(),
          contextMessages.size(),
          totalTokens);

      // 6. 분류 결과로 insight 재구성
      CampaignInsightResult insight = raw.withClassified(positives, negatives, contextMessages);

      // 7. 프롬프트 생성 후 LLM 호출
      String prompt = promptBuilder.build(insight);
      String aiReportText = chatClient.prompt().user(prompt).call().content();

      if (!StringUtils.hasText(aiReportText)) {
        aiReportText = "AI 리포트를 생성할 수 없습니다.";
      }

      // 8. 지표 + AI 리포트 저장
      final Campaign campaign = findCampaign.get();
      createCampaignReportUseCase.execute(
          command.campaignId(),
          new CreateCampaignReportCommand(
              command.roomId(),
              insight.totalViewers(),
              insight.peakConcurrent(),
              insight.avgDurationSeconds(),
              insight.totalMessages(),
              insight.totalQuestions(),
              insight.aiAnswerCount(),
              aiReportText,
              campaign.getStartedAt(),
              campaign.getEndedAt(),
              insight.unansweredQuestions()));

      log.info("AI 리포트 생성 완료 - campaignId={}", command.campaignId());
    } catch (Exception e) {
      log.error("AI 리포트 생성 실패 - campaignId={}: {}", command.campaignId(), e.getMessage(), e);
    }
  }
}
