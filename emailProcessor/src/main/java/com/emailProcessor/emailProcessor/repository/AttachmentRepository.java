package com.emailProcessor.emailProcessor.repository;
import com.emailProcessor.emailProcessor.entity.Attachment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 *  MongoRepository for the Attachment entity.
 */
@Repository
public interface AttachmentRepository extends MongoRepository<Attachment, String> {}
