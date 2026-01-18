package org.giglab.live.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.time.Instant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    JavaTimeModule javaTimeModule = new JavaTimeModule();

    javaTimeModule.addDeserializer(
        Instant.class,
        new JsonDeserializer<>() {
          @Override
          public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String text = p.getText();
            try {
              return Instant.parse(text);
            } catch (Exception e) {
              throw new IOException("Cannot parse Instant: " + text, e);
            }
          }
        });

    mapper.registerModule(javaTimeModule);
    // 활성화 시 숫자 timestamp format 으로 저장됨 (1737162824002)
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    // 알 수 없는 필드 무시
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    return mapper;
  }
}
