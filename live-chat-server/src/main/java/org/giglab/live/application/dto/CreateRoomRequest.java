package org.giglab.live.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * @author : JAKE
 * @date : 26. 1. 11.
 */
@Getter
public class CreateRoomRequest {

  @NotBlank
  @Size(max = 50, message = "채팅방 이름은 50자를 초과할 수 없습니다.")
  private String title;
}
