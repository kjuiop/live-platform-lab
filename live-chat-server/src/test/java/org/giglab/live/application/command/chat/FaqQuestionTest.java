package org.giglab.live.application.command.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import java.util.Map;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActionResponse;
import org.giglab.live.application.dto.action.Actor;
import org.giglab.live.application.service.FaqAnswerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FaqQuestionTest {

  @Mock private FaqAnswerService faqAnswerService;

  @InjectMocks private FaqQuestion handler;

  private final Actor actor = new Actor("u1", "user1", "시청자1");

  @Test
  void actionShouldReturnFaqQuestion() {
    assertThat(handler.action()).isEqualTo(ActionType.FAQ_QUESTION);
  }

  @Test
  void executeShouldReturnEchoAndTriggerAsyncService() {
    // Given
    ActionRequest req =
        new ActionRequest(
            "ROOM_1", "FAQ.QUESTION", actor, Map.of("question", "사이즈가 어떻게 되나요?", "productId", 42));

    // When
    ActionResponse res = handler.execute(req);

    // Then
    assertThat(res.roomId()).isEqualTo("ROOM_1");
    assertThat(res.action()).isEqualTo("FAQ.QUESTION");
    assertThat(res.actor()).isEqualTo(actor);
    assertThat(res.payload()).containsEntry("question", "사이즈가 어떻게 되나요?");
    assertThat(res.sentAt()).isNotNull();
    verify(faqAnswerService).generateAndBroadcast(req);
  }

  @Test
  void executeShouldThrowWhenPayloadIsNull() {
    ActionRequest req = new ActionRequest("ROOM_1", "FAQ.QUESTION", actor, null);

    assertThatThrownBy(() -> handler.execute(req))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("payload.question is required");
  }

  @Test
  void executeShouldThrowWhenQuestionIsBlank() {
    ActionRequest req =
        new ActionRequest(
            "ROOM_1", "FAQ.QUESTION", actor, Map.of("question", "   ", "productId", 1));

    assertThatThrownBy(() -> handler.execute(req))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("payload.question is required");
  }

  @Test
  void executeShouldThrowWhenProductIdIsMissing() {
    ActionRequest req =
        new ActionRequest("ROOM_1", "FAQ.QUESTION", actor, Map.of("question", "색상이 뭐예요?"));

    assertThatThrownBy(() -> handler.execute(req))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("payload.productId must be a positive number");
  }

  @Test
  void executeShouldThrowWhenProductIdIsNotANumber() {
    ActionRequest req =
        new ActionRequest(
            "ROOM_1", "FAQ.QUESTION", actor, Map.of("question", "색상이 뭐예요?", "productId", "abc"));

    assertThatThrownBy(() -> handler.execute(req))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("payload.productId must be a positive number");
  }

  @Test
  void executeShouldThrowWhenProductIdIsZeroOrNegative() {
    ActionRequest req =
        new ActionRequest(
            "ROOM_1", "FAQ.QUESTION", actor, Map.of("question", "색상이 뭐예요?", "productId", 0));

    assertThatThrownBy(() -> handler.execute(req))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("payload.productId must be a positive number");
  }
}
