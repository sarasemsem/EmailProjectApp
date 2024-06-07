package com.emailProcessor.emailProcessor.repository;


import com.emailProcessor.emailProcessor.entity.Email;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailRepository extends MongoRepository<Email, String> {
    @Query(value = "{}", fields = "{ 'emailId': 1, 'sender': 1, 'subject': 1, 'content': 1, 'isRead': 1, 'date': 1}")
    List<Email> findAllEmails(PageRequest pageRequest);
    List<Email> findByTreatedFalse();
    List<Email> findByUrgentTrue();
    List<Email> findByTreatedTrue();

}
