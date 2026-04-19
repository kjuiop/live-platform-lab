package org.giglab.live.commerce.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.api.dto.product.CreateProductRequest;
import org.giglab.live.commerce.api.dto.product.CreateProductResponse;
import org.giglab.live.commerce.api.facade.ProductFacade;
import org.giglab.live.commerce.api.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Product", description = "상품 API")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductFacade productFacade;

  @Operation(summary = "상품 등록", description = "판매자 권한으로 상품을 등록합니다.")
  @PostMapping
  public ResponseEntity<ApiResponse<CreateProductResponse>> create(
      @RequestBody @Valid CreateProductRequest createProductRequest) {
    CreateProductResponse response = productFacade.create(createProductRequest);
    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
