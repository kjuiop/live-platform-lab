package org.giglab.live.commerce.core.product.application.usecase;

import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.dto.CreateProductCommand;
import org.giglab.live.commerce.core.product.application.dto.CreateProductResult;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductStorePort;
import org.giglab.live.commerce.core.product.domain.entity.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateProductUseCase {

  private final ProductStorePort productStorePort;

  public CreateProductResult execute(CreateProductCommand command) {

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

    Product savedProduct = productStorePort.store(product);
    return new CreateProductResult(savedProduct.getId());
  }
}
