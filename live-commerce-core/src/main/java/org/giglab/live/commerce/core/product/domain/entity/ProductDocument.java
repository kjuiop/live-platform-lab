package org.giglab.live.commerce.core.product.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.giglab.live.commerce.core.global.jpa.entity.types.YnType;

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

  @Builder.Default
  @Column(columnDefinition = "varchar(2) default 'N'", nullable = false)
  @Enumerated(EnumType.STRING)
  private YnType embedYn = YnType.N;

  public static ProductDocument pending(String filename, String extractedText) {
    return ProductDocument.builder()
        .filename(filename)
        .extractedText(normalize(extractedText))
        .build();
  }

  public void linkToProduct(Long productId) {
    this.productId = productId;
  }

  public void markAsEmbedded() {
    this.embedYn = YnType.Y;
  }

  private static String normalize(String text) {
    if (text == null) {
      return null;
    }
    return text.trim()
        .replaceAll("\r\n", "\n") // CRLF → LF
        .replaceAll("\n{3,}", "\n\n"); // 3줄 이상 연속 빈 줄 → 최대 1줄 공백
  }
}
