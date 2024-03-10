package com.emailProcessor.emailProcessor.repository;
import com.emailProcessor.emailProcessor.entity.Worker;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * MongoRepositoryfor the Worker entity.
 */

@Repository
public interface WorkerRepository extends MongoRepository<Worker, String> {
    Optional<Worker> findByEmail(String login);
}
