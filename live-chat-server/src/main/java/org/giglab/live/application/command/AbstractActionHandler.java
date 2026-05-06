package org.giglab.live.application.command;

import java.util.Map;
import java.util.function.Supplier;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActionResponse;

public abstract class AbstractActionHandler<ReqT extends ActionRequest, ResT extends ActionResponse>
    implements ActionHandler<ReqT, ResT> {

  @Override
  public final ResT execute(ReqT request) {
    validate(request);
    return process(request);
  }

  protected void validate(ReqT request) {}

  protected abstract ResT process(ReqT request);

  protected void requireString(
      Map<String, Object> payload, String key, Supplier<? extends RuntimeException> ex) {
    Object value = payload == null ? null : payload.get(key);
    if (!(value instanceof String s) || s.isBlank()) {
      throw ex.get();
    }
  }

  protected void requirePositiveLong(
      Map<String, Object> payload, String key, Supplier<? extends RuntimeException> ex) {
    Object value = payload == null ? null : payload.get(key);
    if (!(value instanceof Number n) || n.longValue() <= 0) {
      throw ex.get();
    }
  }
}
