package org.giglab.live.config;

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
  private int corePoolSize;

  @Value("${async.faq.max-pool-size}")
  private int maxPoolSize;

  @Value("${async.faq.queue-capacity}")
  private int queueCapacity;

  @Bean(name = "faqAsyncExecutor")
  public ThreadPoolTaskExecutor faqAsyncExecutor(MeterRegistry meterRegistry) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(corePoolSize);
    executor.setMaxPoolSize(maxPoolSize);
    executor.setQueueCapacity(queueCapacity);
    executor.setThreadNamePrefix("faq-async-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();

    ExecutorServiceMetrics.monitor(
        meterRegistry, executor.getThreadPoolExecutor(), "faq_async_executor");

    return executor;
  }
}
