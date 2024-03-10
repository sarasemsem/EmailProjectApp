package com.emailProcessor.emailProcessor.repository;
import com.emailProcessor.emailProcessor.entity.SecurityRole;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * MongoRepository for the SecurityRole entity.
 */

@Repository
public interface SecurityRoleRepository extends MongoRepository<SecurityRole, String> {}
