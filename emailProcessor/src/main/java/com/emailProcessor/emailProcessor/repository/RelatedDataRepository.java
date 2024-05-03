package com.emailProcessor.emailProcessor.repository;
import com.emailProcessor.emailProcessor.entity.RelatedData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 *  MongoRepository for the RelatedData entity.
 */
@Repository
public interface RelatedDataRepository extends MongoRepository<RelatedData, String> {}
