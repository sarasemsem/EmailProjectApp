package com.emailProcessor.emailProcessor.repository;


import com.emailProcessor.emailProcessor.entity.Email;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends MongoRepository<Email, String> {}