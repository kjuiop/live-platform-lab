package org.giglab.live.commerce.core.product.domain.entity.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmbeddingStatusType {
  NONE("None", "없음"),

  PENDING("Pending", "대기"),

  WORKING("Working", "진행중"),

  DONE("Done", "완료");

  private final String key;

  private final String description;
}
