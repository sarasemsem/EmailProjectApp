package com.emailProcessor.emailProcessor.repository;

import com.emailProcessor.emailProcessor.entity.Action;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 *  MongoRepository for the Action entity.
 */

@Repository
public interface ActionRepository extends MongoRepository<Action, String> {
    Action findByActionId(String actionId);
}

