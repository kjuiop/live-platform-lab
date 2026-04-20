package org.giglab.live.commerce.core.category.infrastructure.persistence;

import static org.giglab.live.commerce.core.category.domain.entity.QCategory.category;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.category.application.dto.CategoryDto;
import org.giglab.live.commerce.core.global.jpa.entity.types.YnType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryQueryRepository {

  private final JPAQueryFactory queryFactory;

  public List<CategoryDto> findAllActive() {
    return queryFactory
        .select(
            Projections.constructor(
                CategoryDto.class,
                category.id,
                category.parent.id,
                category.code,
                category.name,
                category.level,
                category.sortOrder,
                Expressions.constant(Collections.emptyList())))
        .from(category)
        .where(category.deleteYn.eq(YnType.N), category.activeYn.eq(YnType.Y))
        .orderBy(category.level.asc(), category.sortOrder.asc())
        .fetch();
  }
}
