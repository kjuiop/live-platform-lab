package org.giglab.live.collector.application.port;

import org.giglab.live.collector.domain.model.Event;

public interface EventPublisher {

  void publish(Event event);
}
