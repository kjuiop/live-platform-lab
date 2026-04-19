package org.giglab.live.commerce.core.product.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.dto.CreateProductCommand;
import org.giglab.live.commerce.core.product.application.dto.CreateProductResult;
import org.giglab.live.commerce.core.product.application.usecase.CreateProductUseCase;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final CreateProductUseCase createProductUseCase;

  public CreateProductResult create(@Valid CreateProductCommand command) {
    return createProductUseCase.execute(command);
  }
}
