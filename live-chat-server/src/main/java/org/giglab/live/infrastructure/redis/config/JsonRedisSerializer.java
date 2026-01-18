package org.giglab.live.infrastructure.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JsonRedisSerializer implements RedisSerializer<Object> {

  private final ObjectMapper objectMapper;

  @Override
  public byte[] serialize(@Nullable Object value) throws SerializationException {
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
  public @Nullable Object deserialize(byte @Nullable [] bytes) throws SerializationException {
    if (bytes == null || bytes.length == 0) {
      return null;
    }
    try {
      return objectMapper.readValue(bytes, Object.class);
    } catch (Exception e) {
      throw new SerializationException("Could not deserialize", e);
    }
  }
}
