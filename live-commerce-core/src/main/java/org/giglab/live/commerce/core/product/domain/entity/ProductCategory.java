package org.giglab.live.commerce.core.product.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.giglab.live.commerce.core.global.jpa.entity.AuditedEntity;

@Getter
@Builder
@Entity
@Table(
    name = "product_categories",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"product_id", "category_id"})},
    indexes = {
      @Index(
          name = "idx_product_categories_product_id_sort_order_name",
          columnList = "product_id, sort_order, category_name")
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCategory extends AuditedEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(name = "category_id", nullable = false)
  private Long categoryId;

  @Column(name = "category_name")
  private String categoryName;

  @Column(name = "sort_order")
  private int sortOrder;

  public static ProductCategory of(
      Product product, Long categoryId, String categoryName, int sortOrder) {
    return ProductCategory.builder()
        .product(product)
        .categoryId(categoryId)
        .categoryName(categoryName)
        .sortOrder(sortOrder)
        .build();
  }
}
