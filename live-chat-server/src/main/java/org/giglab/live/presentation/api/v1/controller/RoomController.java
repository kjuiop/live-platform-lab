package org.giglab.live.presentation.api.v1.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.application.dto.room.CreateRoomRequest;
import org.giglab.live.application.dto.room.CreateRoomResponse;
import org.giglab.live.application.dto.room.GetRoomResponse;
import org.giglab.live.application.dto.stats.ChatStatsResponse;
import org.giglab.live.application.service.RoomService;
import org.giglab.live.domain.aggregator.ChatMessageAggregator;
import org.giglab.live.presentation.api.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

  private final RoomService roomService;
  private final ChatMessageAggregator chatMessageAggregator;

  @GetMapping
  public ResponseEntity<ApiResponse<List<GetRoomResponse>>> getRooms(
      @RequestParam(defaultValue = "10") @Positive int size) {
    List<GetRoomResponse> responses = roomService.getRooms(size);
    return new ResponseEntity<>(ApiResponse.success(responses), HttpStatus.OK);
  }

  @DeleteMapping("/{roomId}")
  public ResponseEntity<Void> deleteRoom(@PathVariable String roomId) {
    roomService.deleteRoom(roomId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{roomId}/stats")
  public ResponseEntity<ApiResponse<ChatStatsResponse>> getChatStats(@PathVariable String roomId) {
    ChatStatsResponse response = chatMessageAggregator.aggregate(roomId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<CreateRoomResponse>> createRoom(
      @RequestBody @Valid CreateRoomRequest request) {
    CreateRoomResponse response = roomService.createRoom(request);
    return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.CREATED);
  }
}
