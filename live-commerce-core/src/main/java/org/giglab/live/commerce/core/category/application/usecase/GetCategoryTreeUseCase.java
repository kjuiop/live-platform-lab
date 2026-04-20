package org.giglab.live.commerce.core.category.application.usecase;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.category.application.dto.CategoryDto;
import org.giglab.live.commerce.core.category.application.dto.GetCategoryTreeResult;
import org.giglab.live.commerce.core.category.application.port.CategoryQueryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetCategoryTreeUseCase {

  private final CategoryQueryPort categoryQueryPort;

  public GetCategoryTreeResult execute() {
    List<CategoryDto> all = categoryQueryPort.findAllActive();

    // ID를 키로, DTO를 값으로 하는 맵 생성
    Map<Long, CategoryDto> nodeMap = new LinkedHashMap<>();
    for (CategoryDto dto : all) {
      nodeMap.put(
          dto.id(),
          new CategoryDto(
              dto.id(),
              dto.parentId(),
              dto.code(),
              dto.name(),
              dto.level(),
              dto.sortOrder(),
              new ArrayList<>()));
    }

    // 트리 구조로 변환
    List<CategoryDto> roots = new ArrayList<>();
    for (CategoryDto dto : all) {
      CategoryDto node = nodeMap.get(dto.id());
      if (dto.parentId() == null) {
        roots.add(node);
      } else {
        CategoryDto parent = nodeMap.get(dto.parentId());
        if (parent != null) {
          parent.children().add(node);
        }
      }
    }
    return new GetCategoryTreeResult(roots);
  }
}
