package org.giglab.live.commerce.core.campaign.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.giglab.live.commerce.core.global.jpa.entity.AuditedEntity;

@Getter
@Builder
@Entity
@Table(name = "campaign_reports")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CampaignReport extends AuditedEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long campaignId;

  @Column(nullable = false, length = 50, unique = true)
  private String roomId;

  private int totalViewers;
  private int peakConcurrent;
  private long avgDurationSeconds;
  private int totalMessages;
  private int totalQuestions;
  private int aiAnswerCount;

  @Column(columnDefinition = "TEXT")
  private String aiReportText;

  private LocalDateTime startedAt;
  private LocalDateTime endedAt;
}
