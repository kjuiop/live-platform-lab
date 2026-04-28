package org.giglab.live.commerce.core.product.application.dto.pdf;

import org.giglab.live.commerce.core.global.jpa.entity.types.YnType;

public record DocumentSummary(Long documentId, Long productId, String filename, YnType embedYn) {}
