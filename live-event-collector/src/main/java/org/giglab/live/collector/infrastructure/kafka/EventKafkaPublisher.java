package org.giglab.live.collector.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import org.giglab.live.collector.domain.model.Event;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class EventKafkaPublisher {

  private static final String TOPIC = "live-events";

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  public void publish(Event event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send(TOPIC, event.eventId(), payload);
    } catch (Exception e) {
      throw new RuntimeException("Failed to publish event to Kafka", e);
    }
  }
}
