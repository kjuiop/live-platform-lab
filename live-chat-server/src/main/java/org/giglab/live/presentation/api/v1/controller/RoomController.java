package org.giglab.live.presentation.api.v1.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.giglab.live.application.dto.CreateRoomRequest;
import org.giglab.live.application.dto.CreateRoomResponse;
import org.giglab.live.application.dto.GetRoomResponse;
import org.giglab.live.application.service.RoomService;
import org.giglab.live.presentation.api.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : JAKE
 * @date : 26. 1. 11.
 */
@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

  private final RoomService roomService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<GetRoomResponse>>> getRooms(
    @RequestParam(defaultValue = "20") int size
  ) {
    List<GetRoomResponse> responses = roomService.getRooms(size);
    return new ResponseEntity<>(ApiResponse.success(responses), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<ApiResponse<CreateRoomResponse>> createRoom(
    @RequestBody @Valid CreateRoomRequest request
    ) {
    CreateRoomResponse response = roomService.createRoom(request);
    return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.CREATED);
  }
}
