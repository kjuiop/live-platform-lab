package org.giglab.live.commerce.core.campaign.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    name = "campaign_products",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"campaign_id", "product_id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CampaignProduct extends AuditedEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "campaign_id", nullable = false)
  private Campaign campaign;

  @Column(name = "product_id", nullable = false)
  private Long productId;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private int displayOrder;

  protected static CampaignProduct of(
      Campaign campaign, Long productId, String name, int displayOrder) {
    return CampaignProduct.builder()
        .campaign(campaign)
        .productId(productId)
        .name(name)
        .displayOrder(displayOrder)
        .build();
  }
}
