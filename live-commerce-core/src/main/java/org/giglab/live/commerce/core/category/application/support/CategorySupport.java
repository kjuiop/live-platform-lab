package org.giglab.live.commerce.core.category.application.support;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.category.application.port.CategoryQueryPort;
import org.giglab.live.commerce.core.category.domain.exception.CategoryDomainException;
import org.giglab.live.commerce.core.category.domain.exception.CategoryErrorCode;
import org.giglab.live.commerce.core.shared.CategoryInfo;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategorySupport {

  private final CategoryQueryPort categoryQueryPort;

  public List<CategoryInfo> requireExistsAndGetCategory(List<Long> categoryIds) {
    if (categoryIds == null || categoryIds.isEmpty()) {
      throw new CategoryDomainException(CategoryErrorCode.NOT_FOUND);
    }

    List<Long> distinctIds = categoryIds.stream().distinct().toList();

    List<CategoryInfo> categories = categoryQueryPort.findByIds(distinctIds);
    if (categories.size() != distinctIds.size()) {
      throw new CategoryDomainException(CategoryErrorCode.INVALID_CATEGORY);
    }

    return categories;
  }
}
