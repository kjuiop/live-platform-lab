package org.giglab.live.commerce.api.facade;

import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.api.dto.category.GetCategoryTreeResponse;
import org.giglab.live.commerce.api.mapper.category.CategoryMapper;
import org.giglab.live.commerce.core.category.application.CategoryService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryFacade {

  private final CategoryService categoryService;
  private final CategoryMapper categoryMapper;

  public GetCategoryTreeResponse getTree() {
    return categoryMapper.toGetCategoryTreeResponse(categoryService.getCategoryTree());
  }
}
