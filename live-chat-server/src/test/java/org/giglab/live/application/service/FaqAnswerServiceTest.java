package org.giglab.live.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActionResponse;
import org.giglab.live.application.dto.action.Actor;
import org.giglab.live.application.port.external.FaqAnswerPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@ExtendWith(MockitoExtension.class)
class FaqAnswerServiceTest {

  @Mock private FaqAnswerPort faqAnswerPort;
  @Mock private SimpMessagingTemplate operations;

  @InjectMocks private FaqAnswerService service;

  private final Actor actor = new Actor("u1", "user1", "시청자1");

  @Test
  void shouldBroadcastFaqAnswerOnSuccess() {
    // Given
    ActionRequest req =
        new ActionRequest(
            "ROOM_1", "FAQ.QUESTION", actor, Map.of("question", "배송 얼마나 걸려요?", "productId", 10));
    when(faqAnswerPort.ask(10L, "배송 얼마나 걸려요?")).thenReturn("보통 2~3일 소요됩니다.");

    // When
    service.generateAndBroadcast(req);

    // Then
    ArgumentCaptor<ActionResponse> captor = ArgumentCaptor.forClass(ActionResponse.class);
    verify(operations).convertAndSend(eq("/sub/room/ROOM_1"), captor.capture());

    ActionResponse res = captor.getValue();
    assertThat(res.action()).isEqualTo(ActionType.FAQ_ANSWER.getKey());
    assertThat(res.payload()).containsEntry("answer", "보통 2~3일 소요됩니다.");
    assertThat(res.payload()).containsEntry("question", "배송 얼마나 걸려요?");
    assertThat(res.sentAt()).isNotNull();
  }

  @Test
  void shouldBroadcastFallbackAnswerWhenPortReturnsNull() {
    // Given
    ActionRequest req =
        new ActionRequest(
            "ROOM_1", "FAQ.QUESTION", actor, Map.of("question", "성분이 뭐예요?", "productId", 10));
    when(faqAnswerPort.ask(anyLong(), anyString())).thenReturn(null);

    // When
    service.generateAndBroadcast(req);

    // Then
    ArgumentCaptor<ActionResponse> captor = ArgumentCaptor.forClass(ActionResponse.class);
    verify(operations).convertAndSend(eq("/sub/room/ROOM_1"), captor.capture());

    ActionResponse res = captor.getValue();
    assertThat(res.action()).isEqualTo(ActionType.FAQ_ANSWER.getKey());
    assertThat(res.payload()).containsEntry("answer", "답변을 생성할 수 없습니다.");
  }

  @Test
  void shouldBroadcastFaqErrorWhenPortThrows() {
    // Given
    ActionRequest req =
        new ActionRequest(
            "ROOM_1", "FAQ.QUESTION", actor, Map.of("question", "색상이 뭐예요?", "productId", 10));
    when(faqAnswerPort.ask(anyLong(), anyString())).thenThrow(new RuntimeException("AI 서버 오류"));

    // When
    service.generateAndBroadcast(req);

    // Then
    ArgumentCaptor<ActionResponse> captor = ArgumentCaptor.forClass(ActionResponse.class);
    verify(operations).convertAndSend(eq("/sub/room/ROOM_1"), captor.capture());

    ActionResponse res = captor.getValue();
    assertThat(res.action()).isEqualTo(ActionType.FAQ_ERROR.getKey());
    assertThat(res.payload()).containsKey("message");
    assertThat(res.payload()).containsEntry("question", "색상이 뭐예요?");
  }

  @Test
  void shouldBroadcastFaqErrorWhenQuestionIsInvalid() {
    // Given — payload is null, so question validation fails inside try-catch
    ActionRequest req = new ActionRequest("ROOM_1", "FAQ.QUESTION", actor, null);

    // When
    service.generateAndBroadcast(req);

    // Then
    ArgumentCaptor<ActionResponse> captor = ArgumentCaptor.forClass(ActionResponse.class);
    verify(operations).convertAndSend(eq("/sub/room/ROOM_1"), captor.capture());

    ActionResponse res = captor.getValue();
    assertThat(res.action()).isEqualTo(ActionType.FAQ_ERROR.getKey());
    assertThat(res.payload()).containsKey("message");
    verify(faqAnswerPort, never()).ask(anyLong(), any());
  }

  @Test
  void shouldBroadcastFaqErrorWhenProductIdIsInvalid() {
    // Given — productId가 Number가 아닌 경우
    ActionRequest req =
        new ActionRequest(
            "ROOM_1", "FAQ.QUESTION", actor, Map.of("question", "가격이 얼마예요?", "productId", "wrong"));

    // When
    service.generateAndBroadcast(req);

    // Then
    ArgumentCaptor<ActionResponse> captor = ArgumentCaptor.forClass(ActionResponse.class);
    verify(operations).convertAndSend(eq("/sub/room/ROOM_1"), captor.capture());

    ActionResponse res = captor.getValue();
    assertThat(res.action()).isEqualTo(ActionType.FAQ_ERROR.getKey());
    verify(faqAnswerPort, never()).ask(anyLong(), any());
  }
}
