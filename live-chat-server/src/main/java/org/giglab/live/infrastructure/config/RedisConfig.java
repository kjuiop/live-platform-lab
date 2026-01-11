package org.giglab.live.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import tools.jackson.databind.ObjectMapper;

/**
 * @author : JAKE
 * @date : 26. 1. 11.
 */
@Configuration
public class RedisConfig {

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // key 는 String 으로 직렬화
    template.setKeySerializer(RedisSerializer.string());
    template.setHashKeySerializer(RedisSerializer.string());

    // value 는 JSON 으로 직렬화
    template.setValueSerializer(RedisSerializer.json());
    template.setHashValueSerializer(RedisSerializer.json());

    template.afterPropertiesSet();
    return template;
  }
}
