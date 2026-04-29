package org.giglab.live.application.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActionResponse;
import org.giglab.live.application.dto.action.Actor;
import org.giglab.live.domain.exception.ActionErrorCode;
import org.giglab.live.domain.exception.ActionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActionDispatcherTest {

  @Mock private ActionHandler<ActionRequest, ActionResponse> handler;

  private ActionDispatcher dispatcher;

  @BeforeEach
  void setUp() {
    when(handler.action()).thenReturn(ActionType.CHAT_MESSAGE);
    dispatcher = new ActionDispatcher(List.of(handler));
  }

  @Test
  void dispatchShouldThrowWhenActionIsUnsupported() {
    ActionRequest req = new ActionRequest("ROOM_1", "UNKNOWN.ACTION", actor(), Map.of());

    assertThatThrownBy(() -> dispatcher.dispatch(req))
        .isInstanceOf(ActionException.class)
        .hasMessageContaining("Unsupported action")
        .satisfies(
            e ->
                Assertions.assertThat(((ActionException) e).getErrorCode())
                    .isEqualTo(ActionErrorCode.UNSUPPORTED_ACTION));
  }

  private Actor actor() {
    return new Actor("u1", "user1@example.com", "사용자1");
  }
}
