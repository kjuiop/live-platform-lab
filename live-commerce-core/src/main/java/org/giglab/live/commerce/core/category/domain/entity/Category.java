package org.giglab.live.commerce.core.category.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.giglab.live.commerce.core.global.jpa.entity.AuditedEntity;
import org.giglab.live.commerce.core.global.jpa.entity.types.YnType;

@Getter
@Builder
@Entity
@Table(
    name = "categories",
    indexes = {
      // findAllActive / findAllByIdsIn: delete_yn + active_yn 필터 + level/sort_order 정렬
      @Index(
          name = "idx_categories_delete_yn_active_yn_level_sort_order",
          columnList = "delete_yn, active_yn, level, sort_order")
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends AuditedEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String code;

  private String parentCodePath;

  @Column(nullable = false)
  private String name;

  @Builder.Default private int level = 1;

  private int sortOrder;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(length = 2)
  private YnType activeYn = YnType.N;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(length = 2)
  private YnType deleteYn = YnType.N;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private Category parent;

  @Builder.Default
  @OneToMany(
      mappedBy = "parent",
      fetch = FetchType.LAZY,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private List<Category> child = new ArrayList<>();
}
