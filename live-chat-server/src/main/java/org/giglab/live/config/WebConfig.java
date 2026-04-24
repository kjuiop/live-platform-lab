package org.giglab.live.config;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class WebConfig {

  @Value("${cors.allowed-origins}")
  private String allowedOrigin;

  @Value("${commerce-core.http.connect-timeout}")
  private int connectTimeout;

  @Value("${commerce-core.http.read-timeout}")
  private int readTimeout;

  @Value("${commerce-core.http.max-connections}")
  private int maxConnections;

  @Value("${commerce-core.http.max-connections-per-route}")
  private int maxConnectionsPerRoute;

  @Bean
  public RestTemplate restTemplate() {
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(maxConnections);
    connectionManager.setDefaultMaxPerRoute(maxConnectionsPerRoute);

    RequestConfig requestConfig =
        RequestConfig.custom()
            .setConnectionRequestTimeout(connectTimeout, TimeUnit.MILLISECONDS)
            .setResponseTimeout(readTimeout, TimeUnit.MILLISECONDS)
            .build();

    CloseableHttpClient httpClient =
        HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(requestConfig)
            .build();

    return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
  }

  @Bean
  public CorsFilter corsFilter() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of(allowedOrigin));
    config.setAllowedMethods(
        List.of(
            HttpMethod.GET.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.DELETE.name(),
            HttpMethod.PATCH.name(),
            HttpMethod.OPTIONS.name()));
    config.setAllowedHeaders(
        List.of(HttpHeaders.CONTENT_TYPE, HttpHeaders.AUTHORIZATION, HttpHeaders.ACCEPT));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", config);
    source.registerCorsConfiguration("/ws/**", config);
    return new CorsFilter(source);
  }
}
