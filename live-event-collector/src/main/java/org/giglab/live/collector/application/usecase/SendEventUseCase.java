package org.giglab.live.collector.application.usecase;

import lombok.RequiredArgsConstructor;
import org.giglab.live.collector.application.port.EventPublisher;
import org.giglab.live.collector.domain.model.Event;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SendEventUseCase {

  private final EventPublisher eventPublisher;

  public void execute(Event event) {
    eventPublisher.publish(event);
  }
}
