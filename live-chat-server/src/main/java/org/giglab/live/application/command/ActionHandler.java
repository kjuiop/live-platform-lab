package org.giglab.live.application.command;

public interface ActionHandler<RequestT, ResponseT> {
  String action();

  ResponseT execute(RequestT request);
}
