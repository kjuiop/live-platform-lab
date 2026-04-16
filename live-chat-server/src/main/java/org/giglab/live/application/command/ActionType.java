package org.giglab.live.application.command;

import lombok.Getter;

@Getter
public enum ActionType {
  CHAT_MESSAGE("CHAT.MESSAGE"),
  CHAT_JOIN("CHAT.JOIN"),
  CHAT_LEAVE("CHAT.LEAVE"),
  CHAT_SYSTEM("CHAT.SYSTEM");

  private final String key;

  ActionType(String key) {
    this.key = key;
  }
}
