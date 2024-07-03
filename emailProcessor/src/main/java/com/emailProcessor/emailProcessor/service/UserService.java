package com.emailProcessor.emailProcessor.service;
import com.emailProcessor.basedomains.dto.CredentialsDto;
import com.emailProcessor.basedomains.dto.UserDto;
import com.emailProcessor.basedomains.dto.User1Dto;
import com.emailProcessor.emailProcessor.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing
 */
@Service
public interface UserService {

    UserDto login(CredentialsDto credentialsDto);
    /**
     * Save a user.
     *
     * @param user the entity to save.
     * @return the persisted entity.
     */
    ResponseEntity<String> saveUser(User1Dto user);

    /**
     * Updates a user.
     *
     * @param user the entity to update.
     * @return the persisted entity.
     */
    UserDto updateUser(User user);

    /**
     * Partially updates a user.
     *
     * @param user the entity to update partially.
     * @return the persisted entity.
     */
    Optional<UserDto> partialUpdateUser(User user);

    /**
     * Get all the users.
     *
     * @return the list of entities.
     */
    List<User> findAllUsers();

    /**
     * Get the "id" user.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserDto> findOneUser(String id);

    /**
     * Delete the "id" user.
     *
     * @param id the id of the entity.
     */
    void deleteUser(String id);

    UserDto findByLogin(String login);
}
