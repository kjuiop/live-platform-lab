package org.giglab.live.commerce.api.mapper.category;

import org.giglab.live.commerce.api.dto.category.GetCategoryTreeResponse;
import org.giglab.live.commerce.core.category.application.dto.GetCategoryTreeResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
  GetCategoryTreeResponse toGetCategoryTreeResponse(GetCategoryTreeResult result);
}
