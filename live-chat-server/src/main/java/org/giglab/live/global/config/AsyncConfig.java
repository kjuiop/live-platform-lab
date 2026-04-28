package org.giglab.live.global.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class AsyncConfig {

  @Value("${async.faq.core-pool-size}")
  private int faqCorePoolSize;

  @Value("${async.faq.max-pool-size}")
  private int faqMaxPoolSize;

  @Value("${async.faq.queue-capacity}")
  private int faqQueueCapacity;

  @Value("${async.chat.core-pool-size}")
  private int chatCorePoolSize;

  @Value("${async.chat.max-pool-size}")
  private int chatMaxPoolSize;

  @Value("${async.chat.queue-capacity}")
  private int chatQueueCapacity;

  @Bean(name = "faqAsyncExecutor")
  public ThreadPoolTaskExecutor faqAsyncExecutor(MeterRegistry meterRegistry) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(faqCorePoolSize);
    executor.setMaxPoolSize(faqMaxPoolSize);
    executor.setQueueCapacity(faqQueueCapacity);
    executor.setThreadNamePrefix("faq-async-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();

    ExecutorServiceMetrics.monitor(
        meterRegistry, executor.getThreadPoolExecutor(), "faq_async_executor");

    return executor;
  }

  @Bean(name = "chatAsyncExecutor")
  public ThreadPoolTaskExecutor chatAsyncExecutor(MeterRegistry meterRegistry) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(chatCorePoolSize);
    executor.setMaxPoolSize(chatMaxPoolSize);
    executor.setQueueCapacity(chatQueueCapacity);
    executor.setThreadNamePrefix("chat-async-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
    executor.initialize();

    ExecutorServiceMetrics.monitor(
        meterRegistry, executor.getThreadPoolExecutor(), "chat_async_executor");

    return executor;
  }
}
