package com.emailProcessor.emailProcessor.repository;
import com.emailProcessor.emailProcessor.entity.Action;
import com.emailProcessor.emailProcessor.entity.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *  MongoRepository the Category entity.
 */

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {
    @Query("{ 'actions._id' : ?0 }")
    Action findActionWithCategoryId(String categoryId);
}
