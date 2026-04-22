package org.giglab.live.commerce.core.product.application.dto.ai;

import java.util.List;

public record EmbedAllDocumentsContext(int totalCount, List<Long> embeddedDocIds) {}
