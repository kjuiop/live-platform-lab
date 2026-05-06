package org.giglab.live.global.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
public class AsyncConfig {

  @Bean(name = "faqAsyncExecutor")
  public Executor faqAsyncExecutor() {
    return Executors.newVirtualThreadPerTaskExecutor();
  }

  @Bean(name = "chatAsyncExecutor")
  public Executor chatAsyncExecutor() {
    return Executors.newVirtualThreadPerTaskExecutor();
  }
}
