package org.giglab.live.commerce.api.mapper.product;

import org.giglab.live.commerce.api.dto.product.CreateProductRequest;
import org.giglab.live.commerce.api.dto.product.CreateProductResponse;
import org.giglab.live.commerce.core.product.application.dto.CreateProductCommand;
import org.giglab.live.commerce.core.product.application.dto.CreateProductResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  CreateProductCommand toCreateProductCommand(CreateProductRequest createProductRequest);

  CreateProductResponse toCreateProductResponse(CreateProductResult createProductResult);
}
