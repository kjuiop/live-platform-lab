package org.giglab.live.commerce.core.category.application.bridge;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.category.application.support.CategorySupport;
import org.giglab.live.commerce.core.product.application.port.bridge.ProductCategoryAppPort;
import org.giglab.live.commerce.core.shared.CategoryInfo;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductCategoryBridge implements ProductCategoryAppPort {

  private final CategorySupport categorySupport;

  @Override
  public List<CategoryInfo> getCategory(List<Long> categoryIds) {
    return categorySupport.requireExistsAndGetCategory(categoryIds);
  }
}
