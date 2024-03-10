package com.emailProcessor.emailProcessor.repository;
import com.emailProcessor.emailProcessor.entity.LinkedCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 *  MongoRepositoryfor the LinkedCategory entity.
 */
@Repository
public interface LinkedCategoryRepository extends MongoRepository<LinkedCategory, String> {}
