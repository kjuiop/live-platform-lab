package org.giglab.live.infrastructure.mongo.aggregator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.application.dto.stats.ChatStats;
import org.giglab.live.domain.model.ChatMessage;
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
    // query: { roomId: roomId } + group by action
    Aggregation countAgg =
        Aggregation.newAggregation(
            Aggregation.match(Criteria.where("roomId").is(roomId)),
            Aggregation.group("action").count().as("count"));

    // results: [{ _id: "CHAT_MESSAGE", count: 100 }, { _id: "FAQ_QUESTION", count: 20 }, ...]
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

    List<String> unanswered = findUnansweredQuestions(roomId);

    return new ChatStats(totalMessages, totalQuestions, aiAnswerCount, unanswered);
  }

  private List<String> findUnansweredQuestions(String roomId) {
    Query query =
        new Query(
            Criteria.where("roomId")
                .is(roomId)
                .and("action")
                .is(ActionType.FAQ_ANSWER.getKey())
                .and("payload.answer")
                .regex("해당 정보를 찾을 수 없습니다"));

    return mongoTemplate.find(query, ChatMessage.class).stream()
        .map(msg -> msg.getPayload() != null ? (String) msg.getPayload().get("question") : null)
        .filter(q -> q != null && !q.isBlank())
        .collect(Collectors.toList());
  }
}
