package org.animotion.animotionbackend.repository;

import org.animotion.animotionbackend.entity.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
public interface ProjectRepository extends MongoRepository<Project, String> {
    List<Project> findByOwnerId(String ownerId);
    List<Project> findByMemberIdsContains(String userId);
}
