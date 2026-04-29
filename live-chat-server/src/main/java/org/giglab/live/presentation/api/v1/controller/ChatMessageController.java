package org.giglab.live.presentation.api.v1.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.application.dto.ChatMessageResponse;
import org.giglab.live.application.service.ChatMessageService;
import org.giglab.live.presentation.api.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rooms/{roomId}/messages")
public class ChatMessageController {

  private final ChatMessageService chatMessageService;

  @GetMapping
  public ApiResponse<List<ChatMessageResponse>> getRecentMessages(
      @PathVariable String roomId, @RequestParam(defaultValue = "50") int limit) {
    return ApiResponse.success(chatMessageService.getRecentMessages(roomId, limit));
  }
}
