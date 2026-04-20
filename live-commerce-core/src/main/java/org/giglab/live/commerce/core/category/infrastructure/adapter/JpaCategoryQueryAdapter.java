package org.giglab.live.commerce.core.category.infrastructure.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.category.application.dto.CategoryDto;
import org.giglab.live.commerce.core.category.application.port.CategoryQueryPort;
import org.giglab.live.commerce.core.category.infrastructure.persistence.CategoryQueryRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaCategoryQueryAdapter implements CategoryQueryPort {

  private final CategoryQueryRepository queryRepository;

  @Override
  public List<CategoryDto> findAllActive() {
    return queryRepository.findAllActive();
  }
}
