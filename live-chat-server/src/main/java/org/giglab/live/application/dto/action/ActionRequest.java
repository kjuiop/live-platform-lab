package org.giglab.live.application.dto.action;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record ActionRequest(
    @NotBlank String roomId,
    @NotBlank String action,
    @NotNull Actor actor,
    Map<String, Object> payload) {}
