package org.giglab.live.commerce.core.global.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AiTemperatureProperties.class)
public class AiConfig {

  private final AiTemperatureProperties temperature;

  public AiConfig(AiTemperatureProperties temperature) {
    this.temperature = temperature;
  }

  @Bean("classifierChatClient")
  public ChatClient classifierChatClient(ChatModel chatModel) {
    return ChatClient.builder(chatModel)
        .defaultOptions(OpenAiChatOptions.builder().temperature(temperature.classifier()).build())
        .build();
  }

  @Bean("faqChatClient")
  public ChatClient faqChatClient(ChatModel chatModel) {
    return ChatClient.builder(chatModel)
        .defaultOptions(OpenAiChatOptions.builder().temperature(temperature.faq()).build())
        .build();
  }

  @Bean("reportChatClient")
  public ChatClient reportChatClient(ChatModel chatModel) {
    return ChatClient.builder(chatModel)
        .defaultOptions(OpenAiChatOptions.builder().temperature(temperature.report()).build())
        .build();
  }

  @Bean("simulationChatClient")
  public ChatClient simulationChatClient(ChatModel chatModel) {
    return ChatClient.builder(chatModel)
        .defaultOptions(OpenAiChatOptions.builder().temperature(temperature.simulation()).build())
        .build();
  }
}
