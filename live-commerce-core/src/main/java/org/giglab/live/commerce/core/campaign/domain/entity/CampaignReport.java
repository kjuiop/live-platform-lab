package org.giglab.live.commerce.core.campaign.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.giglab.live.commerce.core.global.jpa.entity.AuditedEntity;
import org.giglab.live.commerce.core.global.jpa.entity.types.StringListConverter;

@Getter
@Builder
@Entity
@Table(
    name = "campaign_reports",
    indexes = {@Index(name = "idx_campaign_reports_campaign_id", columnList = "campaign_id")})
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

  @Convert(converter = StringListConverter.class)
  @Column(columnDefinition = "TEXT")
  private List<String> unansweredQuestions;

  private LocalDateTime startedAt;
  private LocalDateTime endedAt;

  public void update(
      int totalViewers,
      int peakConcurrent,
      long avgDurationSeconds,
      int totalMessages,
      int totalQuestions,
      int aiAnswerCount,
      String aiReportText,
      LocalDateTime startedAt,
      LocalDateTime endedAt,
      List<String> unansweredQuestions) {
    this.totalViewers = totalViewers;
    this.peakConcurrent = peakConcurrent;
    this.avgDurationSeconds = avgDurationSeconds;
    this.totalMessages = totalMessages;
    this.totalQuestions = totalQuestions;
    this.aiAnswerCount = aiAnswerCount;
    this.aiReportText = aiReportText;
    this.startedAt = startedAt;
    this.endedAt = endedAt;
    this.unansweredQuestions = unansweredQuestions;
  }

  public static CampaignReport create(
      Long campaignId,
      String roomId,
      int totalViewers,
      int peakConcurrent,
      long avgDurationSeconds,
      int totalMessages,
      int totalQuestions,
      int aiAnswerCount,
      String aiReportText,
      LocalDateTime startedAt,
      LocalDateTime endedAt,
      List<String> unansweredQuestions) {
    return CampaignReport.builder()
        .campaignId(campaignId)
        .roomId(roomId)
        .totalViewers(totalViewers)
        .peakConcurrent(peakConcurrent)
        .avgDurationSeconds(avgDurationSeconds)
        .totalMessages(totalMessages)
        .totalQuestions(totalQuestions)
        .aiAnswerCount(aiAnswerCount)
        .aiReportText(aiReportText)
        .startedAt(startedAt)
        .endedAt(endedAt)
        .unansweredQuestions(unansweredQuestions)
        .build();
  }
}
