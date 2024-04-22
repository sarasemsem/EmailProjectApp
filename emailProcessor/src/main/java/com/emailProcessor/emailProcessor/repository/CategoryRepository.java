package com.emailProcessor.emailProcessor.repository;
import com.emailProcessor.emailProcessor.entity.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 *  MongoRepository the Category entity.
 */

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {
}
