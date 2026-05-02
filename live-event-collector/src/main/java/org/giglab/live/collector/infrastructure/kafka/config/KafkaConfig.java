package org.giglab.live.collector.infrastructure.kafka.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Bean
  public ProducerFactory<String, String> producerFactory() {
    Map<String, Object> config = new HashMap<>();
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    // 모든 레플리카 복제되었는지 확인 후에 ACK 를 받도록 설정하여 데이터 손실 방지
    config.put(ProducerConfig.ACKS_CONFIG, "all");
    config.put(ProducerConfig.RETRIES_CONFIG, 3);
    // 전송 타임아웃
    config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120000);
    // 중복 전송 방지
    config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    // 최대 5개의 요청이 동시에 처리될 수 있도록 설정
    // 한 번에 많이 보내면 성능은 향상되지만 순서 보장 및 안정성에서 문제가 발생할 수 있기 때문에 낮은 값 선호
    config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
    // 메시지를 배치로 묶어서 전송하는 지연 시간 설정 (ms 단위)
    // 이 값이 크면 메시지를 더 많이 배치로 묶어서 자원을 효율적으로 쓰지만 지연이 발생할 수 있음
    config.put(ProducerConfig.LINGER_MS_CONFIG, 5);
    return new DefaultKafkaProducerFactory<>(config);
  }

  @Bean
  public KafkaTemplate<String, String> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }
}
