package org.giglab.live.collector.application;

import lombok.RequiredArgsConstructor;
import org.giglab.live.collector.application.usecase.SendEventUseCase;
import org.giglab.live.collector.domain.model.Event;
import org.giglab.live.collector.presentation.dto.EventRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {

  private final SendEventUseCase sendEventUseCase;

  public void publish(EventRequest request, String remoteAddr) {
    sendEventUseCase.execute(Event.from(request, remoteAddr));
  }
}
