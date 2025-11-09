package org.animotion.animotionbackend.repository;

import org.animotion.animotionbackend.entity.TaskPriority;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TaskPriorityRepository extends MongoRepository<TaskPriority, String> {
    List<TaskPriority> findByProjectId(String projectId);
    List<TaskPriority> findByProjectIdNull();

    Optional<TaskPriority> findByValue(String value);
}