package org.giglab.live.infrastructure.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

@Configuration
public class RedisConfig {

  @Bean
  public RedisTemplate<String, Object> redisTemplate(
      RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // key 는 String 으로 직렬화
    template.setKeySerializer(RedisSerializer.string());
    template.setHashKeySerializer(RedisSerializer.string());

    RedisSerializer<Object> jsonSerializer =
        new RedisSerializer<>() {
          @Override
          public byte[] serialize(@Nullable Object value) {
            if (value == null) {
              return new byte[0];
            }
            try {
              return objectMapper.writeValueAsBytes(value);
            } catch (Exception e) {
              throw new SerializationException("Could not serialize", e);
            }
          }

          @Override
          public @Nullable Object deserialize(byte[] bytes) {
            if (bytes == null || bytes.length == 0) {
              return null;
            }
            try {
              return objectMapper.readValue(bytes, Object.class);
            } catch (Exception e) {
              throw new SerializationException("Could not deserialize", e);
            }
          }
        };

    template.setValueSerializer(jsonSerializer);
    template.setHashValueSerializer(jsonSerializer);
    template.afterPropertiesSet();
    return template;
  }
}
