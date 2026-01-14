package org.giglab.live.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author : JAKE
 * @date : 26. 1. 13.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Message {
  private Long channelId;
  private String sender;
  private String message;
}
