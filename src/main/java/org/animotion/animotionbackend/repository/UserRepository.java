package org.animotion.animotionbackend.repository;

import org.animotion.animotionbackend.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
}
