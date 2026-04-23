package org.giglab.live.infrastructure.client;

import java.net.URI;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.application.port.external.FaqAnswerPort;
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
    @SuppressWarnings("unchecked")
    Map<String, Object> data = (Map<String, Object>) res.getBody().get("data");
    return (String) data.get("answer");
  }
}
