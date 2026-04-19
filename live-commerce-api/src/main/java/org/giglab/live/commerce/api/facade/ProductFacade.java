package org.giglab.live.commerce.api.facade;

import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.api.dto.product.CreateProductRequest;
import org.giglab.live.commerce.api.dto.product.CreateProductResponse;
import org.giglab.live.commerce.api.mapper.product.ProductMapper;
import org.giglab.live.commerce.core.product.application.ProductService;
import org.giglab.live.commerce.core.product.application.dto.CreateProductCommand;
import org.giglab.live.commerce.core.product.application.dto.CreateProductResult;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductFacade {

  private final ProductMapper productMapper;
  private final ProductService productService;

  public CreateProductResponse create(CreateProductRequest request) {
    CreateProductCommand command = productMapper.toCreateProductCommand(request);
    CreateProductResult result = productService.create(command);
    return productMapper.toCreateProductResponse(result);
  }
}
