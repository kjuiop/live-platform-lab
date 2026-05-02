package org.giglab.live.collector.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.giglab.live.collector.application.usecase.SendEventUseCase;
import org.giglab.live.collector.domain.model.Event;
import org.giglab.live.collector.presentation.dto.EventRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

  @Mock private SendEventUseCase sendEventUseCase;

  @InjectMocks private EventService eventService;

  @Test
  @DisplayName("publish() 호출 시 SendEventUseCase.execute()가 Event와 함께 호출된다")
  void publish_callsSendEventUseCase() {
    EventRequest request =
        new EventRequest(
            "ROOM_ENTER",
            "session-abc",
            "user-1",
            "Jake",
            "device-x",
            "room-123",
            "ANDROID",
            "1.0.0",
            null,
            null,
            null,
            null);

    eventService.publish(request, "127.0.0.1");

    verify(sendEventUseCase).execute(any(Event.class));
  }
}
