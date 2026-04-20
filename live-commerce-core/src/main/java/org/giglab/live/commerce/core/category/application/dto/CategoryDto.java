package org.giglab.live.commerce.core.category.application.dto;

import java.util.List;

public record CategoryDto(
    Long id,
    Long parentId,
    String code,
    String name,
    int level,
    int sortOrder,
    List<CategoryDto> children) {}
