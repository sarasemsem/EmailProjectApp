package com.emailProcessor.emailProcessor.repository;
import com.emailProcessor.emailProcessor.entity.EmailProcessingResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailProcessingResultRepository extends MongoRepository<EmailProcessingResult, String> {
}
