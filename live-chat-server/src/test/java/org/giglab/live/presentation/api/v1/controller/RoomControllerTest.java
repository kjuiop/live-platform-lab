package org.giglab.live.presentation.api.v1.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.giglab.live.application.dto.room.CreateRoomRequest;
import org.giglab.live.application.dto.room.CreateRoomResponse;
import org.giglab.live.application.dto.room.GetRoomResponse;
import org.giglab.live.application.dto.stats.ChatStats;
import org.giglab.live.application.dto.stats.ViewerStats;
import org.giglab.live.application.service.RoomService;
import org.giglab.live.infrastructure.mongo.aggregator.ChatMessageAggregator;
import org.giglab.live.infrastructure.mongo.aggregator.ViewerSessionAggregator;
import org.giglab.live.presentation.api.error.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

/**
 * @author : JAKE
 * @date : 26. 1. 11.
 */
@WebMvcTest(RoomController.class)
@Import(GlobalExceptionHandler.class)
class RoomControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private RoomService roomService;
  @MockitoBean private ChatMessageAggregator chatMessageAggregator;
  @MockitoBean private ViewerSessionAggregator viewerSessionAggregator;

  @Test
  @DisplayName("채팅방 생성 성공 - 201 CREATED")
  void createRoomSuccessReturns201() throws Exception {
    // given
    CreateRoomRequest request = new CreateRoomRequest();
    request.setTitle("테스트 방송");

    CreateRoomResponse response =
        new CreateRoomResponse(
            "ROOM_123456789ABC",
            "테스트 방송",
            Instant.parse("2024-01-11T01:00:00Z"),
            Instant.parse("2024-01-11T01:00:00Z"));

    given(roomService.createRoom(any(CreateRoomRequest.class))).willReturn(response);

    // when & then
    mockMvc
        .perform(
            post("/api/v1/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.roomId").value("ROOM_123456789ABC"))
        .andExpect(jsonPath("$.data.title").value("테스트 방송"))
        .andExpect(jsonPath("$.data.createdAt").exists())
        .andExpect(jsonPath("$.data.updatedAt").exists());
  }

  @Test
  @DisplayName("채팅방 생성 실패 - 빈 제목 400 BAD_REQUEST")
  void createRoomEmptyTitleReturns400() throws Exception {
    // given
    CreateRoomRequest request = new CreateRoomRequest();
    request.setTitle(""); // 빈 문자열

    // when & then
    mockMvc
        .perform(
            post("/api/v1/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("E003"))
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  @DisplayName("채팅방 생성 실패 - 50자 초과 제목 400 BAD_REQUEST")
  void createRoomTitleExceeds50CharactersReturns400() throws Exception {
    // given
    CreateRoomRequest request = new CreateRoomRequest();
    request.setTitle("A".repeat(51)); // 51자

    // when & then
    mockMvc
        .perform(
            post("/api/v1/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("E003"))
        .andExpect(
            jsonPath("$.message")
                .value(org.hamcrest.Matchers.containsString("채팅방 이름은 50자를 초과할 수 없습니다.")));
  }

  @Test
  @DisplayName("채팅방 생성 실패 - null 제목 400 BAD_REQUEST")
  void createRoomNullTitleReturns400() throws Exception {
    // given
    CreateRoomRequest request = new CreateRoomRequest();
    request.setTitle(null); // null

    // when & then
    mockMvc
        .perform(
            post("/api/v1/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("E003"));
  }

  @Test
  @DisplayName("채팅방 생성 실패 - 공백만 있는 제목 400 BAD_REQUEST")
  void createRoomBlankTitleReturns400() throws Exception {
    // given
    CreateRoomRequest request = new CreateRoomRequest();
    request.setTitle("   "); // 공백만

    // when & then
    mockMvc
        .perform(
            post("/api/v1/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("E003"));
  }

  @Test
  @DisplayName("채팅방 생성 성공 - 정확히 50자 제목 201 CREATED")
  void createRoomTitleExactly50CharactersReturns201() throws Exception {
    // given
    CreateRoomRequest request = new CreateRoomRequest();
    request.setTitle("A".repeat(50)); // 정확히 50자

    CreateRoomResponse response =
        new CreateRoomResponse("ROOM_123456789ABC", "A".repeat(50), Instant.now(), Instant.now());

    given(roomService.createRoom(any(CreateRoomRequest.class))).willReturn(response);

    // when & then
    mockMvc
        .perform(
            post("/api/v1/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.title").value("A".repeat(50)));
  }

  @Test
  @DisplayName("채팅방 목록 조회 성공 - 최신순 정렬 확인")
  void getRoomsReturnsInLatestOrder() throws Exception {
    // given
    GetRoomResponse latest = new GetRoomResponse("ROOM_003", "최신 채팅방");
    GetRoomResponse middle = new GetRoomResponse("ROOM_002", "중간 채팅방");
    GetRoomResponse oldest = new GetRoomResponse("ROOM_001", "오래된 채팅방");

    List<GetRoomResponse> responses = Arrays.asList(latest, middle, oldest);

    given(roomService.getRooms(3)).willReturn(responses);

    // when & then
    mockMvc
        .perform(get("/api/v1/rooms").param("size", "3"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].roomId").value("ROOM_003")) // 최신이 첫 번째
        .andExpect(jsonPath("$.data[0].title").value("최신 채팅방"))
        .andExpect(jsonPath("$.data[1].roomId").value("ROOM_002"))
        .andExpect(jsonPath("$.data[2].roomId").value("ROOM_001")); // 오래된 것이 마지막
  }

  @Test
  @DisplayName("채팅방 삭제 성공 - 204 NO_CONTENT")
  void deleteRoomSuccessReturns204() throws Exception {
    // given
    String roomId = "ROOM_123456789ABC";
    willDoNothing().given(roomService).deleteRoom(roomId);

    // when & then
    mockMvc
        .perform(delete("/api/v1/rooms/{roomId}", roomId))
        .andDo(print())
        .andExpect(status().isNoContent());

    verify(roomService).deleteRoom(roomId);
  }

  @Test
  @DisplayName("방 통계 조회 성공 - 정상 데이터 200 OK")
  void getRoomStatsReturnsAggregatedData() throws Exception {
    // given
    String roomId = "ROOM_123456789ABC";
    ViewerStats viewerStats = new ViewerStats(50, 20, 142L);
    ChatStats chatStats = new ChatStats(100, 15, 12, List.of());

    given(viewerSessionAggregator.aggregate(roomId)).willReturn(viewerStats);
    given(chatMessageAggregator.aggregate(roomId)).willReturn(chatStats);

    // when & then
    mockMvc
        .perform(get("/api/v1/rooms/{roomId}/stats", roomId))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.totalViewers").value(50))
        .andExpect(jsonPath("$.data.peakConcurrent").value(20))
        .andExpect(jsonPath("$.data.avgDurationSeconds").value(142))
        .andExpect(jsonPath("$.data.totalMessages").value(100))
        .andExpect(jsonPath("$.data.totalQuestions").value(15))
        .andExpect(jsonPath("$.data.aiAnswerCount").value(12));
  }

  @Test
  @DisplayName("방 통계 조회 성공 - 데이터 없을 때 0값으로 200 OK")
  void getRoomStatsReturnsZeroWhenNoData() throws Exception {
    // given
    String roomId = "ROOM_EMPTY00000000";
    ViewerStats viewerStats = new ViewerStats(0, 0, 0L);
    ChatStats chatStats = new ChatStats(0, 0, 0, List.of());

    given(viewerSessionAggregator.aggregate(roomId)).willReturn(viewerStats);
    given(chatMessageAggregator.aggregate(roomId)).willReturn(chatStats);

    // when & then
    mockMvc
        .perform(get("/api/v1/rooms/{roomId}/stats", roomId))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.totalViewers").value(0))
        .andExpect(jsonPath("$.data.peakConcurrent").value(0))
        .andExpect(jsonPath("$.data.avgDurationSeconds").value(0))
        .andExpect(jsonPath("$.data.totalMessages").value(0))
        .andExpect(jsonPath("$.data.totalQuestions").value(0))
        .andExpect(jsonPath("$.data.aiAnswerCount").value(0));
  }

  @Test
  @DisplayName("채팅방 삭제 - 존재하지 않는 roomId도 204 NO_CONTENT (idempotent)")
  void deleteRoomNotExistsReturns204() throws Exception {
    // given
    String roomId = "ROOM_NOTEXISTS0000";
    willDoNothing().given(roomService).deleteRoom(roomId);

    // when & then
    mockMvc
        .perform(delete("/api/v1/rooms/{roomId}", roomId))
        .andDo(print())
        .andExpect(status().isNoContent());

    verify(roomService).deleteRoom(roomId);
  }
}
