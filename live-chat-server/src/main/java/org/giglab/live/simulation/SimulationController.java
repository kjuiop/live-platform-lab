package org.giglab.live.simulation;

import lombok.RequiredArgsConstructor;
import org.giglab.live.presentation.api.response.ApiResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/simulation")
@Profile("local")
@RequiredArgsConstructor
public class SimulationController {

  private final SimulationService simulationService;

  @PostMapping("/run")
  public ResponseEntity<ApiResponse<String>> run(@RequestBody SimulationRequest request) {
    simulationService.run(
        request.roomId(),
        request.productId(),
        request.viewerCount() > 0 ? request.viewerCount() : 8,
        request.messageCount() > 0 ? request.messageCount() : 20);
    return ResponseEntity.ok(ApiResponse.success("시뮬레이션 시작됨"));
  }
}
