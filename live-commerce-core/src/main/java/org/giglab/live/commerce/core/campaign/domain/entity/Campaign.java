package org.giglab.live.commerce.core.campaign.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.giglab.live.commerce.core.campaign.domain.entity.types.BroadcastStatusType;
import org.giglab.live.commerce.core.global.jpa.entity.AuditedEntity;
import org.giglab.live.commerce.core.global.jpa.entity.types.YnType;

@Getter
@Builder
@Entity
@Table(name = "campaigns")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Campaign extends AuditedEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Builder.Default
  @OneToMany(
      mappedBy = "campaign",
      fetch = FetchType.LAZY,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE},
      orphanRemoval = true)
  private List<CampaignProduct> campaignProducts = new ArrayList<>();

  @Builder.Default
  @Column(columnDefinition = "varchar(2) default 'N'", nullable = false)
  @Enumerated(EnumType.STRING)
  private YnType deleteYn = YnType.N;

  @Column(nullable = false, length = 100)
  private String title;

  private String description;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private BroadcastStatusType status = BroadcastStatusType.SCHEDULED;

  private LocalDateTime scheduledAt;

  private LocalDateTime startedAt;

  private LocalDateTime endedAt;

  public static Campaign create(String title, String description, LocalDateTime scheduledAt) {
    return Campaign.builder()
        .title(title)
        .description(description)
        .scheduledAt(scheduledAt)
        .build();
  }

  public void addProduct(Long productId, int displayOrder) {
    this.campaignProducts.add(CampaignProduct.of(this, productId, displayOrder));
  }
}
