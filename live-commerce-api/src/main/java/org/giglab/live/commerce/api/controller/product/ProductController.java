package org.giglab.live.commerce.api.controller.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.api.dto.product.CreateProductRequest;
import org.giglab.live.commerce.api.dto.product.CreateProductResponse;
import org.giglab.live.commerce.api.dto.product.GetDocumentListResponse;
import org.giglab.live.commerce.api.dto.product.GetProductListRequest;
import org.giglab.live.commerce.api.dto.product.GetProductListResponse;
import org.giglab.live.commerce.api.dto.product.GetProductResponse;
import org.giglab.live.commerce.api.dto.product.ParsedProductResponse;
import org.giglab.live.commerce.api.facade.ProductFacade;
import org.giglab.live.commerce.api.response.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Product", description = "상품 API")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductFacade productFacade;

  @Operation(summary = "상품 목록 조회", description = "커서 기반 페이지네이션으로 상품 목록을 조회합니다.")
  @GetMapping
  public ResponseEntity<ApiResponse<GetProductListResponse>> getList(
      @Valid GetProductListRequest request) {
    GetProductListResponse response = productFacade.getList(request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "상품 상세 조회", description = "상품 상세정보를 조회합니다.")
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<GetProductResponse>> getDetail(@PathVariable Long id) {
    GetProductResponse response = productFacade.getDetail(id);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "상품 등록", description = "상품을 등록합니다.")
  @PostMapping
  public ResponseEntity<ApiResponse<CreateProductResponse>> create(
      @RequestBody @Valid CreateProductRequest createProductRequest) {
    CreateProductResponse response = productFacade.create(createProductRequest);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "상품 문서 목록 조회", description = "상품에 연결된 PDF 문서 목록을 조회합니다.")
  @GetMapping("/{id}/documents")
  public ResponseEntity<ApiResponse<GetDocumentListResponse>> getDocumentList(
      @PathVariable Long id) {
    GetDocumentListResponse response = productFacade.getDocumentList(id);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "PDF 파싱", description = "PDF에서 상품 정보를 추출합니다. 상품 등록 전 사용.")
  @PostMapping(value = "/pdf/parse", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<ParsedProductResponse>> parsePdf(
      @RequestParam("file") MultipartFile file) {
    ParsedProductResponse response = productFacade.parsePdf(file);
    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
