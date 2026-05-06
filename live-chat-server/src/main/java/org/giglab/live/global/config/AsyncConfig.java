package org.giglab.live.global.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
public class AsyncConfig {

  @Bean(name = "faqAsyncExecutor", destroyMethod = "shutdown")
  public ExecutorService faqAsyncExecutor(MeterRegistry meterRegistry) {
    var executor =
        Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name("faq-async-", 0).factory());
    return ExecutorServiceMetrics.monitor(meterRegistry, executor, "faq_async_executor");
  }

  @Bean(name = "chatAsyncExecutor", destroyMethod = "shutdown")
  public ExecutorService chatAsyncExecutor(MeterRegistry meterRegistry) {
    var executor =
        Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name("chat-async-", 0).factory());
    return ExecutorServiceMetrics.monitor(meterRegistry, executor, "chat_async_executor");
  }
}
