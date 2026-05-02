package org.giglab.live.collector.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import org.giglab.live.collector.application.EventService;
import org.giglab.live.collector.global.config.JacksonConfig;
import org.giglab.live.collector.presentation.dto.EventRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(EventController.class)
@Import(JacksonConfig.class)
class EventControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private EventService eventService;

  @Test
  @DisplayName("유효한 요청 - 202 Accepted 응답 및 eventService.publish() 호출")
  void collectEvent_validRequest_returns202AndCallsPublish() throws Exception {
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
            Map.of("key", "value"));

    mockMvc
        .perform(
            post("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isAccepted());

    verify(eventService).publish(any(EventRequest.class), any(String.class));
  }

  @Test
  @DisplayName("eventType 누락 - 400 Bad Request")
  void collectEvent_missingEventType_returns400() throws Exception {
    String body =
        """
        {"sessionId":"session-abc","roomId":"room-123"}
        """;

    mockMvc
        .perform(post("/api/v1/events").contentType(MediaType.APPLICATION_JSON).content(body))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyNoInteractions(eventService);
  }

  @Test
  @DisplayName("sessionId 누락 - 400 Bad Request")
  void collectEvent_missingSessionId_returns400() throws Exception {
    String body =
        """
        {"eventType":"ROOM_ENTER","roomId":"room-123"}
        """;

    mockMvc
        .perform(post("/api/v1/events").contentType(MediaType.APPLICATION_JSON).content(body))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyNoInteractions(eventService);
  }

  @Test
  @DisplayName("roomId 누락 - 400 Bad Request")
  void collectEvent_missingRoomId_returns400() throws Exception {
    String body =
        """
        {"eventType":"ROOM_ENTER","sessionId":"session-abc"}
        """;

    mockMvc
        .perform(post("/api/v1/events").contentType(MediaType.APPLICATION_JSON).content(body))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyNoInteractions(eventService);
  }

  @Test
  @DisplayName("eventType 공백 - 400 Bad Request")
  void collectEvent_blankEventType_returns400() throws Exception {
    EventRequest request =
        new EventRequest(
            "  ", "session-abc", null, null, null, "room-123", null, null, null, null, null, null);

    mockMvc
        .perform(
            post("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyNoInteractions(eventService);
  }
}
