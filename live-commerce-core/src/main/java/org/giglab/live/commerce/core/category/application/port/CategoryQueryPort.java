package org.giglab.live.commerce.core.category.application.port;

import java.util.List;
import org.giglab.live.commerce.core.category.application.dto.CategoryDto;
import org.giglab.live.commerce.core.shared.CategoryInfo;

public interface CategoryQueryPort {
  List<CategoryDto> findAllActive();

  List<CategoryInfo> findByIds(List<Long> distinctIds);
}
