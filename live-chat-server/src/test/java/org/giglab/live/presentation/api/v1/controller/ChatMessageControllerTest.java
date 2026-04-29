package org.giglab.live.presentation.api.v1.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.giglab.live.application.dto.ChatMessageResponse;
import org.giglab.live.application.service.ChatMessageService;
import org.giglab.live.presentation.api.error.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChatMessageController.class)
@Import(GlobalExceptionHandler.class)
class ChatMessageControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ChatMessageService chatMessageService;

  @Test
  @DisplayName("최근 메시지 조회 성공 - roomId 경로 파라미터와 limit 전달")
  void getRecentMessages_successWithRoomIdAndLimit() throws Exception {
    // given
    String roomId = "ROOM_ABC";
    List<ChatMessageResponse> messages =
        List.of(
            new ChatMessageResponse(
                "id1",
                roomId,
                "CHAT.MESSAGE",
                "u1",
                "사용자1",
                Map.of("message", "안녕하세요"),
                Instant.parse("2024-01-01T10:00:00Z"),
                1L),
            new ChatMessageResponse(
                "id2",
                roomId,
                "CHAT.MESSAGE",
                "u2",
                "사용자2",
                Map.of("message", "반갑습니다"),
                Instant.parse("2024-01-01T10:01:00Z"),
                2L));
    given(chatMessageService.getRecentMessages(roomId, 50)).willReturn(messages);

    // when & then
    mockMvc
        .perform(get("/api/v1/rooms/{roomId}/messages", roomId).param("limit", "50"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(2))
        .andExpect(jsonPath("$.data[0].id").value("id1"))
        .andExpect(jsonPath("$.data[0].roomId").value(roomId))
        .andExpect(jsonPath("$.data[0].action").value("CHAT.MESSAGE"))
        .andExpect(jsonPath("$.data[1].id").value("id2"));

    verify(chatMessageService).getRecentMessages(roomId, 50);
  }

  @Test
  @DisplayName("최근 메시지 조회 - limit 기본값 100 적용")
  void getRecentMessages_defaultLimit100() throws Exception {
    // given
    String roomId = "ROOM_DEF";
    given(chatMessageService.getRecentMessages(roomId, 100)).willReturn(List.of());

    // when & then
    mockMvc
        .perform(get("/api/v1/rooms/{roomId}/messages", roomId))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray());

    verify(chatMessageService).getRecentMessages(roomId, 100);
  }

  @Test
  @DisplayName("최근 메시지 조회 - 메시지 없는 방은 빈 배열 반환")
  void getRecentMessages_emptyRoom_returnsEmptyArray() throws Exception {
    // given
    String roomId = "ROOM_EMPTY";
    given(chatMessageService.getRecentMessages(roomId, 100)).willReturn(List.of());

    // when & then
    mockMvc
        .perform(get("/api/v1/rooms/{roomId}/messages", roomId))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @Test
  @DisplayName("최근 메시지 조회 - 응답에 sentAt 필드 포함")
  void getRecentMessages_responseSentAtExists() throws Exception {
    // given
    String roomId = "ROOM_GHI";
    List<ChatMessageResponse> messages =
        List.of(
            new ChatMessageResponse(
                "id1",
                roomId,
                "FAQ.ANSWER",
                "ai",
                "AI 어시스턴트",
                Map.of("answer", "2~3일 소요"),
                Instant.parse("2024-01-01T10:00:00Z"),
                1L));
    given(chatMessageService.getRecentMessages(roomId, 100)).willReturn(messages);

    // when & then
    mockMvc
        .perform(get("/api/v1/rooms/{roomId}/messages", roomId))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].sentAt").exists())
        .andExpect(jsonPath("$.data[0].action").value("FAQ.ANSWER"));
  }
}
