package org.giglab.live.commerce.core.global.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

  @Value("${http.client.connect-timeout:3000}")
  private int connectTimeoutMs;

  @Value("${http.client.read-timeout:5000}")
  private int readTimeoutMs;

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder
        .connectTimeout(Duration.ofMillis(connectTimeoutMs))
        .readTimeout(Duration.ofMillis(readTimeoutMs))
        .build();
  }
}
