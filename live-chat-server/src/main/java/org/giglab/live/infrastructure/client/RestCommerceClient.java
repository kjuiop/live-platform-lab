package org.giglab.live.infrastructure.client;

import java.net.URI;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.application.port.external.FaqAnswerPort;
import org.giglab.live.simulation.SimulationMessages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestCommerceClient implements FaqAnswerPort {

  private final RestTemplate restTemplate;

  @Value("${commerce-core.url}")
  private String commerceCoreUrl;

  @Value("${commerce-core.base-path}")
  private String commerceCoreBasePath;

  @Override
  public String ask(Long productId, String question) {
    String url = commerceCoreUrl + commerceCoreBasePath + "/products/" + productId + "/ai/ask";
    Map<String, String> body = Map.of("question", question);
    var res =
        restTemplate.exchange(
            RequestEntity.post(URI.create(url)).body(body),
            new ParameterizedTypeReference<Map<String, Object>>() {});

    Map<String, Object> responseBody = res.getBody();
    if (responseBody == null) {
      throw new IllegalStateException("commerce-core 응답 body가 비어 있습니다.");
    }

    Object dataField = responseBody.get("data");
    if (!(dataField instanceof Map)) {
      throw new IllegalStateException(
          "commerce-core 응답에서 data 필드를 찾을 수 없습니다. error=" + responseBody.get("error"));
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> data = (Map<String, Object>) dataField;
    Object answer = data.get("answer");
    if (!(answer instanceof String)) {
      throw new IllegalStateException("commerce-core 응답에서 answer 필드를 찾을 수 없습니다.");
    }

    return (String) answer;
  }

  public SimulationMessages getSimulationMessages(Long productId) {
    String url =
        commerceCoreUrl + commerceCoreBasePath + "/products/" + productId + "/simulation-messages";
    var res =
        restTemplate.exchange(
            RequestEntity.get(URI.create(url)).build(),
            new ParameterizedTypeReference<Map<String, Object>>() {});

    Map<String, Object> body = res.getBody();
    if (body == null) {
      return null;
    }

    Object dataField = body.get("data");
    if (!(dataField instanceof Map)) {
      return null;
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> data = (Map<String, Object>) dataField;

    @SuppressWarnings("unchecked")
    List<String> chatMessages = (List<String>) data.getOrDefault("chatMessages", List.of());
    @SuppressWarnings("unchecked")
    List<String> faqQuestions = (List<String>) data.getOrDefault("faqQuestions", List.of());

    return new SimulationMessages(chatMessages, faqQuestions);
  }
}
