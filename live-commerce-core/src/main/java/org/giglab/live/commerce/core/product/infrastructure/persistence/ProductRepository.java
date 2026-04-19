package org.giglab.live.commerce.core.product.infrastructure.persistence;

import org.giglab.live.commerce.core.product.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {}
