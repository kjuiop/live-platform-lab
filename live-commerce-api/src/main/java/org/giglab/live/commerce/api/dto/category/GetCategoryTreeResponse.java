package org.giglab.live.commerce.api.dto.category;

import java.util.List;

public record GetCategoryTreeResponse(List<CategoryItemResponse> categories) {

  public record CategoryItemResponse(
      Long id,
      String code,
      String name,
      int level,
      int sortOrder,
      List<CategoryItemResponse> children) {}
}
