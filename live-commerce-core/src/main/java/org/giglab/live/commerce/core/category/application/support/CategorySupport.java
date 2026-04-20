package org.giglab.live.commerce.core.category.application.support;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.category.application.port.CategoryQueryPort;
import org.giglab.live.commerce.core.category.domain.entity.Category;
import org.giglab.live.commerce.core.shared.CategoryInfo;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategorySupport {

  private final CategoryQueryPort categoryQueryPort;

  public List<CategoryInfo> requireExistsAndGetCategory(List<Long> categoryIds) {
    if (categoryIds == null || categoryIds.isEmpty()) {
      throw new IllegalStateException("Category IDs cannot be null or empty");
    }

    List<Long> distinctIds = categoryIds.stream().distinct().toList();

    List<Category> categories = categoryQueryPort.findByIds(distinctIds);
    if (categories.size() != distinctIds.size()) {
      throw new IllegalStateException("Some categories not found");
    }

    Map<Long, Category> categoryMap =
        categories.stream().collect(Collectors.toMap(Category::getId, category -> category));

    return categoryIds.stream()
        .map(
            id -> {
              Category category = categoryMap.get(id);
              if (category == null) {
                throw new IllegalStateException("Category not found");
              }
              return new CategoryInfo(category.getId(), category.getName());
            })
        .toList();
  }
}
