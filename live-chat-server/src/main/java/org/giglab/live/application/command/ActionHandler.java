package org.giglab.live.application.command;

public interface ActionHandler<RequestT, ResponseT> {
  ActionType action();

  ResponseT execute(RequestT request);
}
