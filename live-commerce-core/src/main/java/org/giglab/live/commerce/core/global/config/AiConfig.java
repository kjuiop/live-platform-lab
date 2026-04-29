package org.giglab.live.commerce.core.global.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

  @Bean("classifierChatClient")
  public ChatClient classifierChatClient(ChatModel chatModel) {
    return ChatClient.builder(chatModel)
        .defaultOptions(OpenAiChatOptions.builder().temperature(0.0).build())
        .build();
  }

  @Bean("faqChatClient")
  public ChatClient faqChatClient(ChatModel chatModel) {
    return ChatClient.builder(chatModel)
        .defaultOptions(OpenAiChatOptions.builder().temperature(0.2).build())
        .build();
  }

  @Bean("reportChatClient")
  public ChatClient reportChatClient(ChatModel chatModel) {
    return ChatClient.builder(chatModel)
        .defaultOptions(OpenAiChatOptions.builder().temperature(0.3).build())
        .build();
  }

  @Bean("simulationChatClient")
  public ChatClient simulationChatClient(ChatModel chatModel) {
    return ChatClient.builder(chatModel)
        .defaultOptions(OpenAiChatOptions.builder().temperature(0.8).build())
        .build();
  }
}
