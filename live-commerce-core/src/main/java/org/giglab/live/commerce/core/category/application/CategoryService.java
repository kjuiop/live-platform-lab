package org.giglab.live.commerce.core.category.application;

import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.category.application.dto.GetCategoryTreeResult;
import org.giglab.live.commerce.core.category.application.usecase.GetCategoryTreeUseCase;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final GetCategoryTreeUseCase getCategoryTreeUseCase;

  public GetCategoryTreeResult getCategoryTree() {
    return getCategoryTreeUseCase.execute();
  }
}
