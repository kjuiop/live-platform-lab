package org.giglab.live.presentation.api.v1.controller;

import org.giglab.live.application.dto.CreateRoomRequest;
import org.giglab.live.application.dto.CreateRoomResponse;
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

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author : JAKE
 * @date : 26. 1. 11.
 */
@WebMvcTest(RoomController.class)
@Import(GlobalExceptionHandler.class)
class RoomControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private RoomService roomService;

  @Test
  @DisplayName("채팅방 생성 성공 - 201 CREATED")
  void createRoom_Success_Returns201() throws Exception {
    // given
    CreateRoomRequest request = new CreateRoomRequest();
    request.setTitle("테스트 방송");

    CreateRoomResponse response = new CreateRoomResponse(
      "ROOM_123456789ABC",
      "테스트 방송",
      LocalDateTime.of(2024, 1, 11, 10, 0, 0),
      LocalDateTime.of(2024, 1, 11, 10, 0, 0)
    );

    given(roomService.createRoom(any(CreateRoomRequest.class)))
      .willReturn(response);

    // when & then
    mockMvc.perform(post("/api/v1/rooms")
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
  void createRoom_EmptyTitle_Returns400() throws Exception {
    // given
    CreateRoomRequest request = new CreateRoomRequest();
    request.setTitle("");  // 빈 문자열

    // when & then
    mockMvc.perform(post("/api/v1/rooms")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.code").value("E003"))
      .andExpect(jsonPath("$.message").exists());
  }

  @Test
  @DisplayName("채팅방 생성 실패 - 50자 초과 제목 400 BAD_REQUEST")
  void createRoom_TitleExceeds50Characters_Returns400() throws Exception {
    // given
    CreateRoomRequest request = new CreateRoomRequest();
    request.setTitle("A".repeat(51));  // 51자

    // when & then
    mockMvc.perform(post("/api/v1/rooms")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.code").value("E003"))
      .andExpect(jsonPath("$.message")
        .value(org.hamcrest.Matchers.containsString("채팅방 이름은 50자를 초과할 수 없습니다.")));
  }

  @Test
  @DisplayName("채팅방 생성 실패 - null 제목 400 BAD_REQUEST")
  void createRoom_NullTitle_Returns400() throws Exception {
    // given
    CreateRoomRequest request = new CreateRoomRequest();
    request.setTitle(null);  // null

    // when & then
    mockMvc.perform(post("/api/v1/rooms")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.code").value("E003"));
  }

  @Test
  @DisplayName("채팅방 생성 실패 - 공백만 있는 제목 400 BAD_REQUEST")
  void createRoom_BlankTitle_Returns400() throws Exception {
    // given
    CreateRoomRequest request = new CreateRoomRequest();
    request.setTitle("   ");  // 공백만

    // when & then
    mockMvc.perform(post("/api/v1/rooms")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.code").value("E003"));
  }

  @Test
  @DisplayName("채팅방 생성 성공 - 정확히 50자 제목 201 CREATED")
  void createRoom_TitleExactly50Characters_Returns201() throws Exception {
    // given
    CreateRoomRequest request = new CreateRoomRequest();
    request.setTitle("A".repeat(50));  // 정확히 50자

    CreateRoomResponse response = new CreateRoomResponse(
      "ROOM_123456789ABC",
      "A".repeat(50),
      LocalDateTime.now(),
      LocalDateTime.now()
    );

    given(roomService.createRoom(any(CreateRoomRequest.class)))
      .willReturn(response);

    // when & then
    mockMvc.perform(post("/api/v1/rooms")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andDo(print())
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.data.title").value("A".repeat(50)));
  }
}