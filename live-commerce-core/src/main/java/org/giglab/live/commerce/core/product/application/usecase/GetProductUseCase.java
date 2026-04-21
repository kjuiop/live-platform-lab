package org.giglab.live.commerce.core.product.application.usecase;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.dto.GetProductResult;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductQueryPort;
import org.giglab.live.commerce.core.product.domain.entity.Product;
import org.giglab.live.commerce.core.product.domain.exception.ProductDomainException;
import org.giglab.live.commerce.core.product.domain.exception.ProductErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetProductUseCase {

  private final ProductQueryPort productQueryPort;

  public GetProductResult execute(Long productId) {
    Optional<Product> fetched = productQueryPort.findById(productId);
    if (fetched.isEmpty()) {
      throw new ProductDomainException(ProductErrorCode.NOTFOUND_PRODUCT);
    }
    Product product = fetched.get();
    return GetProductResult.from(
        product.getId(),
        product.getName(),
        product.getStatus(),
        product.getDescription(),
        product.getPrice(),
        product.getStockQuantity(),
        product.getSortOrder(),
        product.getManufacturer(),
        product.getIngredients(),
        product.getUsageMethod(),
        product.getCreatedAt(),
        product.getUpdatedAt());
  }
}
