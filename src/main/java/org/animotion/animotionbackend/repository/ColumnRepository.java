package org.animotion.animotionbackend.repository;

import org.animotion.animotionbackend.entity.Column;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
public interface ColumnRepository extends MongoRepository<Column, String> {
    List<Column> findAllByProjectId(String projectId);
}
