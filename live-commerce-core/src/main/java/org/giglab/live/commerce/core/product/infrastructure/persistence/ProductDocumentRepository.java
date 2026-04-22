package org.giglab.live.commerce.core.product.infrastructure.persistence;

import org.giglab.live.commerce.core.product.domain.entity.ProductDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDocumentRepository extends JpaRepository<ProductDocument, Long> {}
