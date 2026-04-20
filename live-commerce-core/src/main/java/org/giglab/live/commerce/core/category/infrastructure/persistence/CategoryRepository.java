package org.giglab.live.commerce.core.category.infrastructure.persistence;

import java.util.Optional;
import org.giglab.live.commerce.core.category.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  Optional<Category> findByCode(String code);
}
