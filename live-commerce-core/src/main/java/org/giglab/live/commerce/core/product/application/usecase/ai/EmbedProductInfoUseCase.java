package org.giglab.live.commerce.core.product.application.usecase.ai;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.dto.ai.EmbedProductInfoResult;
import org.giglab.live.commerce.core.product.application.dto.ai.ProductInfoMetadata;
import org.giglab.live.commerce.core.product.application.port.ai.EmbedDocumentPort;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductStorePort;
import org.giglab.live.commerce.core.product.domain.entity.Product;
import org.giglab.live.commerce.core.product.domain.entity.types.EmbeddingStatusType;
import org.giglab.live.commerce.core.product.domain.exception.ProductDomainException;
import org.giglab.live.commerce.core.product.domain.exception.ProductErrorCode;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EmbedProductInfoUseCase {

  private final ProductStorePort productStorePort;
  private final EmbedDocumentPort embedDocumentPort;

  public EmbedProductInfoResult execute(Long productId) {

    Optional<Product> findProduct = productStorePort.findEntityById(productId);
    if (findProduct.isEmpty()) {
      throw new ProductDomainException(
          ProductErrorCode.PRODUCT_NOT_FOUND,
          String.format("상품을 찾을 수 없습니다. productId=%d", productId));
    }

    Product product = findProduct.get();
    if (product.getEmbeddingStatus() == EmbeddingStatusType.DONE) {
      throw new ProductDomainException(
          ProductErrorCode.PRODUCT_ALREADY_EMBEDDED,
          String.format("이미 임베딩된 상품입니다. productId=%d", productId));
    }

    String text = buildProductText(product);

    ProductInfoMetadata metadata = new ProductInfoMetadata(productId);

    Document doc = new Document(text, metadata.toMap());
    embedDocumentPort.embed(List.of(doc)); // 상품 정보는 청킹 없이 1개 문서로

    product.markInfoAsEmbedded();

    return new EmbedProductInfoResult(product.getId(), product.getEmbeddingStatus());
  }

  private String buildProductText(Product product) {
    return String.format(
        """
        상품명: %s
        설명: %s
        가격: %s원
        제조사: %s
        성분: %s
        사용법: %s
        """,
        nullSafe(product.getName()),
        nullSafe(product.getDescription()),
        product.getPrice() != null ? product.getPrice().toPlainString() : "정보 없음",
        nullSafe(product.getManufacturer()),
        nullSafe(product.getIngredients()),
        nullSafe(product.getUsageMethod()));
  }

  private String nullSafe(String value) {
    return value != null ? value : "정보 없음";
  }
}
