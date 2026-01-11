package org.giglab.live.domain.model;

import java.util.UUID;

/**
 * @author : JAKE
 * @date : 26. 1. 11.
 */
public class RoomIdGenerator {

  private static final String PREFIX = "ROOM";

  protected static String generate() {
    String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
    return String.format("%s_%s", PREFIX, uuid.substring(0, 12).toUpperCase());
  }
}
