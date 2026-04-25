package org.giglab.live.domain.aggregator;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.giglab.live.application.command.ActionType;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessageAggregator {

  private final MongoTemplate mongoTemplate;

  public ChatStats aggregate(String roomId) {
    // action별 메시지 수 집계
    Aggregation countAgg =
        Aggregation.newAggregation(
            Aggregation.match(Criteria.where("roomId").is(roomId)),
            Aggregation.group("action").count().as("count"));

    AggregationResults<Map> countResults =
        mongoTemplate.aggregate(countAgg, "chat_messages", Map.class);

    int totalMessages = 0;
    int totalQuestions = 0;
    int aiAnswerCount = 0;

    for (Map row : countResults.getMappedResults()) {
      String action = (String) row.get("_id");
      int count = ((Number) row.get("count")).intValue();

      if (ActionType.CHAT_MESSAGE.getKey().equals(action)) {
        totalMessages = count;
      } else if (ActionType.FAQ_QUESTION.getKey().equals(action)) {
        totalQuestions = count;
      } else if (ActionType.FAQ_ANSWER.getKey().equals(action)) {
        aiAnswerCount = count;
      }
    }

    // 미답변 질문 추출: FAQ_QUESTION 중 FAQ_ANSWER에 없는 질문
    List<String> unanswered = findUnansweredQuestions(roomId);

    return new ChatStats(totalMessages, totalQuestions, aiAnswerCount, unanswered);
  }

  private List<String> findUnansweredQuestions(String roomId) {
    List<String> questions = findPayloadQuestions(roomId, ActionType.FAQ_QUESTION.getKey());
    Set<String> answered = Set.copyOf(findPayloadQuestions(roomId, ActionType.FAQ_ANSWER.getKey()));

    return questions.stream().filter(q -> !answered.contains(q)).toList();
  }

  private List<String> findPayloadQuestions(String roomId, String action) {
    Query query = new Query(Criteria.where("roomId").is(roomId).and("action").is(action));

    return mongoTemplate.find(query, org.giglab.live.domain.model.ChatMessage.class).stream()
        .map(msg -> msg.getPayload() != null ? (String) msg.getPayload().get("question") : null)
        .filter(q -> q != null && !q.isBlank())
        .collect(Collectors.toList());
  }

  public record ChatStats(
      int totalMessages, int totalQuestions, int aiAnswerCount, List<String> unansweredQuestions) {}
}
