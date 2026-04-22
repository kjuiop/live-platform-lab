package org.giglab.live.commerce.core.product.application.usecase;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.dto.CreateProductCommand;
import org.giglab.live.commerce.core.product.application.dto.CreateProductResult;
import org.giglab.live.commerce.core.product.application.port.bridge.ProductCategoryAppPort;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductDocumentStorePort;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductStorePort;
import org.giglab.live.commerce.core.product.domain.entity.Product;
import org.giglab.live.commerce.core.product.domain.entity.ProductDocument;
import org.giglab.live.commerce.core.product.domain.exception.ProductDomainException;
import org.giglab.live.commerce.core.product.domain.exception.ProductErrorCode;
import org.giglab.live.commerce.core.shared.CategoryInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateProductUseCase {

  private final ProductCategoryAppPort productCategoryAppPort;
  private final ProductStorePort productStorePort;
  private final ProductDocumentStorePort productDocumentStorePort;

  public CreateProductResult execute(CreateProductCommand command) {

    List<CategoryInfo> categoryInfos = productCategoryAppPort.getCategory(command.categoryIds());

    Product product =
        Product.create(
            command.name(),
            command.description(),
            command.price(),
            command.stockQuantity(),
            command.sortOrder(),
            command.manufacturer(),
            command.ingredients(),
            command.usageMethod());

    for (int i = 0; i < categoryInfos.size(); i++) {
      CategoryInfo categoryInfo = categoryInfos.get(i);
      product.addCategory(categoryInfo.categoryId(), categoryInfo.categoryName(), i);
    }

    Product savedProduct = productStorePort.store(product);

    if (command.documentId() != null) {
      Optional<ProductDocument> findDocument =
          productDocumentStorePort.findEntityById(command.documentId());
      if (findDocument.isEmpty()) {
        throw new ProductDomainException(
            ProductErrorCode.PDF_NOT_FOUND,
            String.format("PDF 문서를 찾을 수 없습니다. documentId=%d", command.documentId()));
      }
      ProductDocument document = findDocument.get();
      document.linkToProduct(savedProduct.getId());
    }

    return new CreateProductResult(savedProduct.getId());
  }
}
