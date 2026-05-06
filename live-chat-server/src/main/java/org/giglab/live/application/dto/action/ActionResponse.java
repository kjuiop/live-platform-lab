package org.giglab.live.application.dto.action;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.Instant;
import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "action", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = DefaultActionResponse.class, name = "CHAT.MESSAGE"),
  @JsonSubTypes.Type(value = DefaultActionResponse.class, name = "CHAT.LEAVE"),
  @JsonSubTypes.Type(value = DefaultActionResponse.class, name = "CHAT.SYSTEM"),
  @JsonSubTypes.Type(value = DefaultActionResponse.class, name = "FAQ.QUESTION"),
  @JsonSubTypes.Type(value = DefaultActionResponse.class, name = "FAQ.ANSWER"),
  @JsonSubTypes.Type(value = DefaultActionResponse.class, name = "FAQ.ERROR"),
  @JsonSubTypes.Type(value = DefaultActionResponse.class, name = "VIEWER.COUNT"),
  @JsonSubTypes.Type(value = JoinRoomResponse.class, name = "CHAT.JOIN"),
  @JsonSubTypes.Type(value = ShowBannerResponse.class, name = "PRODUCT.BANNER.ON"),
  @JsonSubTypes.Type(value = HideBannerResponse.class, name = "PRODUCT.BANNER.OFF"),
})
public sealed interface ActionResponse
    permits DefaultActionResponse, JoinRoomResponse, ShowBannerResponse, HideBannerResponse {

  String roomId();

  String action();

  Actor actor();

  Instant sentAt();

  long seq();

  ActionResponse withSeq(long seq);

  // DefaultActionResponse 외 서브클래스에서 payload가 없는 경우 null 반환
  default Map<String, Object> payload() {
    return null;
  }
}
