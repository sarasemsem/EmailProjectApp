package com.emailProcessor.emailProcessor.repository;

import com.emailProcessor.emailProcessor.entity.Sender;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Mongo Repository for the Sender entity.
 */

@Repository
public interface SenderRepository extends MongoRepository<Sender, String> {
    Optional<Sender> findSenderBySenderEmail(String email);
}
