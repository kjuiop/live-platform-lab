package org.giglab.live.commerce.core.product.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.dto.CreateProductCommand;
import org.giglab.live.commerce.core.product.application.dto.CreateProductResult;
import org.giglab.live.commerce.core.product.application.dto.GetProductListResult;
import org.giglab.live.commerce.core.product.application.dto.GetProductResult;
import org.giglab.live.commerce.core.product.application.dto.ProductListQuery;
import org.giglab.live.commerce.core.product.application.usecase.CreateProductUseCase;
import org.giglab.live.commerce.core.product.application.usecase.GetProductListUseCase;
import org.giglab.live.commerce.core.product.application.usecase.GetProductUseCase;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final GetProductUseCase getProductUseCase;
  private final GetProductListUseCase getProductListUseCase;
  private final CreateProductUseCase createProductUseCase;

  public GetProductListResult getList(ProductListQuery query) {
    return getProductListUseCase.execute(query);
  }

  public GetProductResult getDetail(Long productId) {
    return getProductUseCase.execute(productId);
  }

  public CreateProductResult create(@Valid CreateProductCommand command) {
    return createProductUseCase.execute(command);
  }
}
