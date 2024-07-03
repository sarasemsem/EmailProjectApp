package com.emailProcessor.emailProcessor.repository;

import com.emailProcessor.emailProcessor.entity.ActionParam;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionParamsRepository extends MongoRepository<ActionParam, String> {
}
