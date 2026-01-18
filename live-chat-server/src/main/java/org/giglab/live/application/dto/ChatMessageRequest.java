package org.giglab.live.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequest {
  @NotNull private String roomId;
  @NotNull private String username;
  @NotBlank private String sender;
  @NotBlank private String message;
}
