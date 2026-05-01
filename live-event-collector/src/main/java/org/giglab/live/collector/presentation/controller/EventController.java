package org.giglab.live.collector.presentation.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.giglab.live.collector.application.EventService;
import org.giglab.live.collector.presentation.dto.EventRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

  private final EventService eventService;

  @PostMapping
  public ResponseEntity<Void> collectEvent(
      @RequestBody @Valid EventRequest request, HttpServletRequest httpRequest) {
    eventService.publish(request, httpRequest.getRemoteAddr());
    return ResponseEntity.accepted().build();
  }
}
