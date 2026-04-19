package org.giglab.live.commerce.core.product.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.giglab.live.commerce.core.global.jpa.entity.AuditedEntity;
import org.giglab.live.commerce.core.global.jpa.entity.types.YnType;
import org.giglab.live.commerce.core.product.domain.entity.types.EmbeddingStatusType;
import org.giglab.live.commerce.core.product.domain.entity.types.ProductStatusType;

@Getter
@Builder
@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends AuditedEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Builder.Default
  @OneToMany(
      mappedBy = "product",
      fetch = FetchType.LAZY,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE},
      orphanRemoval = true)
  private List<ProductCategory> productCategories = new ArrayList<>();

  @Builder.Default
  @Column(columnDefinition = "varchar(2) default 'N'", nullable = false)
  @Enumerated(EnumType.STRING)
  private YnType deleteYn = YnType.N;

  @Builder.Default
  @Column(nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private ProductStatusType status = ProductStatusType.DRAFT;

  @Builder.Default
  @Column(nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private EmbeddingStatusType embeddingStatus = EmbeddingStatusType.NONE;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  @Lob
  private String description;

  @Column(nullable = false)
  private BigDecimal price;

  private int stockQuantity;

  private int sortOrder;

  @Column(length = 200)
  private String manufacturer;

  @Lob private String ingredients;

  @Lob private String usageMethod;

  public static Product create(
      String name,
      String description,
      BigDecimal price,
      int stockQuantity,
      int sortOrder,
      String manufacturer,
      String ingredients,
      String usageMethod) {
    return Product.builder()
        .name(name)
        .description(description)
        .price(price)
        .stockQuantity(stockQuantity)
        .sortOrder(sortOrder)
        .status(ProductStatusType.ON_SALE)
        .manufacturer(manufacturer)
        .ingredients(ingredients)
        .usageMethod(usageMethod)
        .build();
  }

  public void addCategory(Long categoryId, String categoryName, int sortOrder) {
    productCategories.add(ProductCategory.of(this, categoryId, categoryName, sortOrder));
  }
}
