package org.giglab.live.infrastructure.mongo;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.domain.model.ChatMessage;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MongoChatInsightRepository {

  private static final int RAW_SAMPLE_CAP = 100;

  private final MongoTemplate mongoTemplate;

  public List<String> findRawMessages(String roomId) {
    Aggregation agg =
        Aggregation.newAggregation(
            Aggregation.match(
                Criteria.where("roomId")
                    .is(roomId)
                    .and("action")
                    .is(ActionType.CHAT_MESSAGE.getKey())),
            Aggregation.sample(RAW_SAMPLE_CAP));

    return mongoTemplate
        .aggregate(agg, ChatMessage.class, ChatMessage.class)
        .getMappedResults()
        .stream()
        .map(msg -> (String) msg.getPayload().get("message"))
        .filter(Objects::nonNull)
        .toList();
  }
}
