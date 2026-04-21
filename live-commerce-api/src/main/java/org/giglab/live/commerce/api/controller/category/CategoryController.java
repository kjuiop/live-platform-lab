package org.giglab.live.commerce.api.controller.category;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.api.dto.category.GetCategoryTreeResponse;
import org.giglab.live.commerce.api.facade.CategoryFacade;
import org.giglab.live.commerce.api.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category", description = "카테고리 API")
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryFacade categoryFacade;

  @Operation(summary = "카테고리 트리 조회", description = "카테고리 트리를 조회합니다.")
  @GetMapping
  public ResponseEntity<ApiResponse<GetCategoryTreeResponse>> getTree() {
    GetCategoryTreeResponse response = categoryFacade.getTree();
    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
