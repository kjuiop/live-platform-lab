package org.giglab.live.commerce.api.mapper.product;

import org.giglab.live.commerce.api.dto.product.CreateProductRequest;
import org.giglab.live.commerce.api.dto.product.CreateProductResponse;
import org.giglab.live.commerce.api.dto.product.GetProductListRequest;
import org.giglab.live.commerce.api.dto.product.GetProductListResponse;
import org.giglab.live.commerce.api.dto.product.GetProductResponse;
import org.giglab.live.commerce.core.product.application.dto.CreateProductCommand;
import org.giglab.live.commerce.core.product.application.dto.CreateProductResult;
import org.giglab.live.commerce.core.product.application.dto.GetProductListResult;
import org.giglab.live.commerce.core.product.application.dto.GetProductResult;
import org.giglab.live.commerce.core.product.application.dto.ProductListQuery;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  CreateProductCommand toCreateProductCommand(CreateProductRequest createProductRequest);

  CreateProductResponse toCreateProductResponse(CreateProductResult createProductResult);

  GetProductListResponse toGetProductListResponse(GetProductListResult result);

  ProductListQuery toProductListQuery(GetProductListRequest request);

  GetProductResponse toGetProductResponse(GetProductResult result);
}
