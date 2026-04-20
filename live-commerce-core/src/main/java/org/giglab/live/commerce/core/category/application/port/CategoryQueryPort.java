package org.giglab.live.commerce.core.category.application.port;

import java.util.List;
import org.giglab.live.commerce.core.category.application.dto.CategoryDto;
import org.giglab.live.commerce.core.category.domain.entity.Category;

public interface CategoryQueryPort {
  List<CategoryDto> findAllActive();

  List<Category> findByIds(List<Long> distinctIds);
}
