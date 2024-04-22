package com.emailProcessor.emailProcessor.repository;


import com.emailProcessor.emailProcessor.entity.Email;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailRepository extends MongoRepository<Email, String> {
    List<Email> findByTreatedFalse();
    List<Email> findByUrgentTrue();
    List<Email> findByTreatedTrue();

}
