package org.giglab.live.commerce.core.category.application.port;

import java.util.List;
import org.giglab.live.commerce.core.category.application.dto.CategoryDto;

public interface CategoryQueryPort {
  List<CategoryDto> findAllActive();
}
