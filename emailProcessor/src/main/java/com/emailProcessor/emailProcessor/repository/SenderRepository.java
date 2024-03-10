package com.emailProcessor.emailProcessor.repository;

import com.emailProcessor.emailProcessor.entity.Sender;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * MongoRepositoryfor the Sender entity.
 */

@Repository
public interface SenderRepository extends MongoRepository<Sender, String> {}
