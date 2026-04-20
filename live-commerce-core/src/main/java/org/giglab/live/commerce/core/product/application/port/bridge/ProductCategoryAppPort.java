package org.giglab.live.commerce.core.product.application.port.bridge;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.giglab.live.commerce.core.shared.CategoryInfo;

public interface ProductCategoryAppPort {
  List<CategoryInfo> getCategory(@Size(min = 1) @NotNull List<Long> categoryIds);
}
