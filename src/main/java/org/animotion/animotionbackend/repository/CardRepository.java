package org.animotion.animotionbackend.repository;

import org.animotion.animotionbackend.entity.Card;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
public interface CardRepository extends MongoRepository<Card, String> {
    List<Card> findByColumnId(String columnId);
    List<Card> findAllByProjectId(String projectId);
}
