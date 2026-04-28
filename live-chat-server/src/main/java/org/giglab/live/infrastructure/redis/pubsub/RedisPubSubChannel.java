package org.giglab.live.infrastructure.redis.pubsub;

public final class RedisPubSubChannel {

  private RedisPubSubChannel() {}

  private static final String ROOM_PREFIX = "LIVE:ROOM:";

  public static String roomChannel(String roomId) {
    return ROOM_PREFIX + roomId;
  }

  // RedisMessageListenerContainer 패턴 구독용
  public static String roomPattern() {
    return ROOM_PREFIX + "*";
  }

  // 채널명 → roomId 역변환
  public static String extractRoomId(String channel) {
    return channel.substring(ROOM_PREFIX.length());
  }
}
