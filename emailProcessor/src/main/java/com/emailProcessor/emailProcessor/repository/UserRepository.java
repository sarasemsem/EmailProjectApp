package com.emailProcessor.emailProcessor.repository;
import com.emailProcessor.emailProcessor.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * MongoRepositoryfor the Worker entity.
 */

@Repository
@EnableRedisRepositories
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String login);
}
