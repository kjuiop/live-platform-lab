package org.giglab.live.commerce.api.facade;

import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.api.dto.product.CreateProductRequest;
import org.giglab.live.commerce.api.dto.product.CreateProductResponse;
import org.giglab.live.commerce.api.dto.product.GetProductListRequest;
import org.giglab.live.commerce.api.dto.product.GetProductListResponse;
import org.giglab.live.commerce.api.dto.product.GetProductResponse;
import org.giglab.live.commerce.api.mapper.product.ProductMapper;
import org.giglab.live.commerce.core.product.application.ProductService;
import org.giglab.live.commerce.core.product.application.dto.CreateProductCommand;
import org.giglab.live.commerce.core.product.application.dto.CreateProductResult;
import org.giglab.live.commerce.core.product.application.dto.GetProductListResult;
import org.giglab.live.commerce.core.product.application.dto.GetProductResult;
import org.giglab.live.commerce.core.product.application.dto.ProductListQuery;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductFacade {

  private final ProductMapper productMapper;
  private final ProductService productService;

  public GetProductListResponse getList(GetProductListRequest request) {
    ProductListQuery query = productMapper.toProductListQuery(request);
    GetProductListResult result = productService.getList(query);
    return productMapper.toGetProductListResponse(result);
  }

  public CreateProductResponse create(CreateProductRequest request) {
    CreateProductCommand command = productMapper.toCreateProductCommand(request);
    CreateProductResult result = productService.create(command);
    return productMapper.toCreateProductResponse(result);
  }

  public GetProductResponse getDetail(Long id) {
    GetProductResult result = productService.getDetail(id);
    return productMapper.toGetProductResponse(result);
  }
}
