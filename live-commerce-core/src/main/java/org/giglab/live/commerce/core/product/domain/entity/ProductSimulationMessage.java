package org.giglab.live.commerce.core.product.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
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
    name = "product_simulation_messages",
    indexes = {
      @Index(name = "idx_product_simulation_messages_product_id", columnList = "product_id")
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductSimulationMessage extends AuditedEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long productId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private MessageType messageType;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  public enum MessageType {
    CHAT,
    FAQ
  }

  public static ProductSimulationMessage create(
      Long productId, MessageType messageType, String content) {
    return ProductSimulationMessage.builder()
        .productId(productId)
        .messageType(messageType)
        .content(content)
        .build();
  }
}
