package org.giglab.live.commerce.api.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AskProductQuestionRequest(@NotBlank @Size(max = 500) String question) {}
