package com.emailProcessor.emailProcessor.repository;


import com.emailProcessor.emailProcessor.entity.Email;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface EmailRepository extends MongoRepository<Email, String> {
    @Query(value = "{}")
    List<Email> findAllEmails(PageRequest pageRequest);
    @Query(value = "{}", fields = "{ 'emailId': 1, 'sender': 1, 'subject': 1, 'content': 1, 'isRead': 1, 'date': 1}")
    List<Email> findAllEmail(Pageable pageable);
    List<Email> findByTreatedFalse();
    List<Email> findByUrgentTrue();
    List<Email> findByTreatedTrue();
    List<Email> findByTreatedFalse(Pageable pageable);

}
