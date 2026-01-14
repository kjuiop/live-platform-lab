package org.giglab.live.presentation.api.v1.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.giglab.live.application.dto.CreateRoomRequest;
import org.giglab.live.application.dto.CreateRoomResponse;
import org.giglab.live.application.dto.GetRoomResponse;
import org.giglab.live.application.service.RoomService;
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
            LocalDateTime.of(2024, 1, 11, 10, 0, 0),
            LocalDateTime.of(2024, 1, 11, 10, 0, 0));

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
        new CreateRoomResponse(
            "ROOM_123456789ABC", "A".repeat(50), LocalDateTime.now(), LocalDateTime.now());

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
}
