package org.giglab.live.commerce.core.product.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.giglab.live.commerce.core.global.jpa.entity.AuditedEntity;

@Getter
@Builder
@Entity
@Table(name = "product_documents")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ProductDocument extends AuditedEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long productId;

  @Column(nullable = false)
  private String filename;

  @Lob
  @Column(nullable = false, columnDefinition = "LONGTEXT")
  private String extractedText;

  public static ProductDocument pending(String filename, String extractedText) {
    return ProductDocument.builder().filename(filename).extractedText(extractedText).build();
  }

  public void linkToProduct(Long productId) {
    this.productId = productId;
  }
}
