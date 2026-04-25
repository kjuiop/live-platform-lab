package org.giglab.live.infrastructure.mongo;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.domain.model.ChatMessage;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MongoChatInsightRepository {

  private static final List<String> POSITIVE_KEYWORDS =
      List.of("좋아요", "사고싶어", "구매할게", "완전", "짱", "최고", "추천", "맛있어", "예쁘다", "가성비");
  private static final List<String> NEGATIVE_KEYWORDS =
      List.of("비싸요", "별로", "아쉬워요", "불편", "실망", "취소", "환불", "불만", "비추");
  private static final int MAX_SAMPLE_SIZE = 10;

  private final MongoTemplate mongoTemplate;

  public List<String> findPositiveMessages(String roomId) {
    return findByKeywords(roomId, POSITIVE_KEYWORDS);
  }

  public List<String> findNegativeMessages(String roomId) {
    return findByKeywords(roomId, NEGATIVE_KEYWORDS);
  }

  private List<String> findByKeywords(String roomId, List<String> keywords) {
    String pattern = String.join("|", keywords);
    Query query =
        new Query(
                Criteria.where("roomId")
                    .is(roomId)
                    .and("action")
                    .is(ActionType.CHAT_MESSAGE.getKey())
                    .and("payload.message")
                    .regex(pattern))
            .limit(MAX_SAMPLE_SIZE);

    return mongoTemplate.find(query, ChatMessage.class).stream()
        .map(msg -> (String) msg.getPayload().get("message"))
        .toList();
  }
}
