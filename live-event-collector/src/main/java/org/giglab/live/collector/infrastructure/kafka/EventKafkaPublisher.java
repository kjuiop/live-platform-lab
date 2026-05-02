package org.giglab.live.collector.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.collector.domain.model.Event;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventKafkaPublisher {

  private static final String TOPIC = "live-events";

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  public void publish(Event event) {
    String payload = objectMapper.writeValueAsString(event);
    kafkaTemplate
        .send(TOPIC, event.eventId(), payload)
        .whenComplete(
            (result, ex) -> {
              if (ex != null) {
                log.error("Kafka send failed: topic={}, eventId={}", TOPIC, event.eventId(), ex);
              }
            });
  }
}
