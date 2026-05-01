package org.giglab.live.collector.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.giglab.live.collector.application.port.EventPublisher;
import org.giglab.live.collector.domain.model.Event;
import org.giglab.live.collector.infrastructure.kafka.EventKafkaPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisher implements EventPublisher {

  private final EventKafkaPublisher kafkaPublisher;

  @Override
  public void publish(Event event) {
    kafkaPublisher.publish(event);
  }
}
