package org.giglab.live.commerce.core.product.application.usecase;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.dto.CreateProductCommand;
import org.giglab.live.commerce.core.product.application.dto.CreateProductResult;
import org.giglab.live.commerce.core.product.application.port.bridge.ProductCategoryAppPort;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductStorePort;
import org.giglab.live.commerce.core.product.domain.entity.Product;
import org.giglab.live.commerce.core.shared.CategoryInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateProductUseCase {

  private final ProductCategoryAppPort productCategoryAppPort;
  private final ProductStorePort productStorePort;

  public CreateProductResult execute(CreateProductCommand command) {

    List<CategoryInfo> categoryInfos = productCategoryAppPort.getCategory(command.categoryIds());

    Product product =
        Product.create(
            command.name(),
            command.description(),
            command.price(),
            command.stockQuantity(),
            command.sortOrder(),
            command.manufacturer(),
            command.ingredients(),
            command.usageMethod());

    for (int i = 0; i < categoryInfos.size(); i++) {
      CategoryInfo categoryInfo = categoryInfos.get(i);
      product.addCategory(categoryInfo.categoryId(), categoryInfo.categoryName(), i);
    }

    Product savedProduct = productStorePort.store(product);
    return new CreateProductResult(savedProduct.getId());
  }
}
