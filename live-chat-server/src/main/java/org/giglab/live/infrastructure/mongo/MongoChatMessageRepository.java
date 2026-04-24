package org.giglab.live.infrastructure.mongo;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.domain.model.ChatMessage;
import org.giglab.live.domain.repository.ChatMessageRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MongoChatMessageRepository implements ChatMessageRepository {

  private final MongoTemplate mongoTemplate;

  @Override
  public void save(ChatMessage message) {
    mongoTemplate.save(message);
  }

  @Override
  public List<ChatMessage> findRecentByRoomId(String roomId, int limit) {
    Query query =
        new Query(Criteria.where("roomId").is(roomId))
            .with(Sort.by(Sort.Direction.ASC, "sentAt"))
            .limit(limit);
    return mongoTemplate.find(query, ChatMessage.class);
  }
}
