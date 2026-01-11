package org.giglab.live.presentation.api.v1.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.giglab.live.application.dto.CreateRoomRequest;
import org.giglab.live.application.dto.CreateRoomResponse;
import org.giglab.live.application.service.RoomService;
import org.giglab.live.presentation.api.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author : JAKE
 * @date : 26. 1. 11.
 */
@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

  private final RoomService roomService;

  @PostMapping
  @ResponseBody
  public ResponseEntity<ApiResponse> createRoom(
    @RequestBody @Valid CreateRoomRequest request
    ) {
    CreateRoomResponse response = roomService.createRoom(request);
    return new ResponseEntity<>(ApiResponse.OK(response), HttpStatus.CREATED);
  }
}
