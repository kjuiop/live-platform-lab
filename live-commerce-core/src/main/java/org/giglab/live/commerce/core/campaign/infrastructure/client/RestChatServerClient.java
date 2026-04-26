package org.giglab.live.commerce.core.campaign.infrastructure.client;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignInsightResult;
import org.giglab.live.commerce.core.campaign.application.port.external.ChatRoomCreatePort;
import org.giglab.live.commerce.core.campaign.application.port.external.ChatRoomDeletePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestChatServerClient implements ChatRoomCreatePort, ChatRoomDeletePort {

  private final RestTemplate restTemplate;

  @Value("${chat.server.url}")
  private String chatServerUrl;

  @Override
  public String createRoom(String title) {
    String url = chatServerUrl + "/api/v1/rooms";
    HttpEntity<CreateRoomRequest> request = new HttpEntity<>(new CreateRoomRequest(title));

    ResponseEntity<ChatApiResponse<CreateRoomResponse>> response =
        restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {});

    ChatApiResponse<CreateRoomResponse> body = response.getBody();
    if (body == null || body.data() == null || body.data().roomId() == null) {
      throw new IllegalStateException("채팅방 생성 응답이 없습니다.");
    }

    log.info("채팅방 생성 완료 - roomId={}, title={}", body.data().roomId(), title);
    return body.data().roomId();
  }

  @Async
  @Override
  public void deleteRoom(String roomId) {
    try {
      String url = chatServerUrl + "/api/v1/rooms/" + roomId;
      restTemplate.exchange(
          url, HttpMethod.DELETE, HttpEntity.EMPTY, new ParameterizedTypeReference<Void>() {});
      log.info("채팅방 삭제 완료 - roomId={}", roomId);
    } catch (Exception e) {
      log.warn("채팅방 삭제 실패 (chat-server) - roomId={}", roomId, e);
    }
  }

  public CampaignInsightResult getInsight(String roomId) {
    String url = chatServerUrl + "/api/v1/rooms/" + roomId + "/insight";

    ResponseEntity<ChatApiResponse<CampaignInsightResult>> response =
        restTemplate.exchange(
            url, HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<>() {});

    ChatApiResponse<CampaignInsightResult> body = response.getBody();
    if (body == null || body.data() == null) {
      throw new IllegalStateException("채팅방 인사이트 응답이 없습니다. roomId=" + roomId);
    }

    log.debug("채팅방 인사이트 조회 완료 - roomId={}", roomId);
    return body.data();
  }

  private record CreateRoomRequest(String title) {}

  private record CreateRoomResponse(
      String roomId, String title, Instant createdAt, Instant updatedAt) {}

  private record ChatApiResponse<T>(T data) {}
}
