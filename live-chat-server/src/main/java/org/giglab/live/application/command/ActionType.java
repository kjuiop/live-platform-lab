package org.giglab.live.application.command;

import lombok.Getter;

@Getter
public enum ActionType {
  CHAT_MESSAGE("CHAT.MESSAGE");

  private final String key;

  ActionType(String key) {
    this.key = key;
  }
}
