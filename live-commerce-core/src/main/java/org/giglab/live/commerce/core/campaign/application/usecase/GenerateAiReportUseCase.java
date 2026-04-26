package org.giglab.live.commerce.core.campaign.application.usecase;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignInsightResult;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignReportCommand;
import org.giglab.live.commerce.core.campaign.application.dto.GenerateAiReportCommand;
import org.giglab.live.commerce.core.campaign.application.port.persistence.CampaignStorePort;
import org.giglab.live.commerce.core.campaign.application.prompt.ChatInsightPromptBuilder;
import org.giglab.live.commerce.core.campaign.domain.entity.Campaign;
import org.giglab.live.commerce.core.campaign.domain.exception.CampaignDomainException;
import org.giglab.live.commerce.core.campaign.domain.exception.CampaignErrorCode;
import org.giglab.live.commerce.core.campaign.infrastructure.client.RestChatServerClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
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
  private final ChatClient chatClient;

  public GenerateAiReportUseCase(
      CampaignStorePort campaignStorePort,
      RestChatServerClient chatServerClient,
      ChatInsightPromptBuilder promptBuilder,
      CreateCampaignReportUseCase createCampaignReportUseCase,
      ChatModel chatModel) {
    this.campaignStorePort = campaignStorePort;
    this.chatServerClient = chatServerClient;
    this.promptBuilder = promptBuilder;
    this.createCampaignReportUseCase = createCampaignReportUseCase;
    this.chatClient = ChatClient.create(chatModel);
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

      Campaign campaign = findCampaign.get();

      // 1. live-chat-server에서 집계 + 전처리 데이터 조회
      CampaignInsightResult insight = chatServerClient.getInsight(command.roomId());

      // 2. 프롬프트 생성 후 LLM 호출
      String prompt = promptBuilder.build(insight);
      String aiReportText = chatClient.prompt().user(prompt).call().content();

      if (!StringUtils.hasText(aiReportText)) {
        aiReportText = "AI 리포트를 생성할 수 없습니다.";
      }

      // 3. 지표 + AI 리포트 저장 (기존 upsert 로직 재사용)
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
              campaign.getEndedAt()));

      log.info("AI 리포트 생성 완료 - campaignId={}", command.campaignId());
    } catch (Exception e) {
      log.error("AI 리포트 생성 실패 - campaignId={}: {}", command.campaignId(), e.getMessage(), e);
    }
  }
}
