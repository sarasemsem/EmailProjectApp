package com.emailProcessor.emailProcessor.controller;
import com.emailProcessor.basedomains.dto.User1Dto;
import com.emailProcessor.basedomains.dto.UserDto;
import com.emailProcessor.emailProcessor.controller.errors.BadRequestException;
import com.emailProcessor.emailProcessor.entity.User;
import com.emailProcessor.emailProcessor.repository.UserRepository;
import com.emailProcessor.emailProcessor.service.UserService;
import com.mongodb.lang.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link com.emailProcessor.emailProcessor.entity.User}.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    private static final String ENTITY_NAME = "user";
    private final UserService userService;

    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * {@code POST  /users} : Create a new user.
     *
     * @param userDto the user to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new worker, or with status {@code 400 (Bad Request)} if the worker has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<String> createUser(@Validated @RequestBody User1Dto userDto) throws URISyntaxException {
        log.debug("REST request to save User : {}", userDto);

        try {
            ResponseEntity<String> result = userService.saveUser(userDto);
            return result;
        } catch (BadRequestException e) {
            log.error("Bad request alert exception", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error saving user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving user");
        }
    }

    /**
     * {@code PUT  /users/:userId} : Updates an existing user.
     *
     * @param userId the id of the User to save.
     * @param user the User to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated user,
     * or with status {@code 400 (Bad Request)} if the user is not valid,
     * or with status {@code 500 (Internal Server Error)} if the user couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(
        @PathVariable(value = "userId", required = false) final String userId,
        @Validated @RequestBody User user
    ) throws URISyntaxException {
        log.debug("REST request to update user : {}, {}", userId, user);
        if (user.getUserId() == null) {
            throw new BadRequestException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(userId, user.getUserId())) {
            throw new BadRequestException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userRepository.existsById(userId)) {
            throw new BadRequestException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UserDto result = userService.updateUser(user);
        return ResponseEntity
                .ok()
                .body(result);
    }

    /**
     * {@code PATCH  /users/:userId} : Partial updates given fields of an existing user, field will ignore if it is null
     *
     * @param userId the id of the user to save.
     * @param user the worker to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated user,
     * or with status {@code 400 (Bad Request)} if the user is not valid,
     * or with status {@code 404 (Not Found)} if the user is not found,
     * or with status {@code 500 (Internal Server Error)} if the user couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{userId}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Optional<UserDto>> partialUpdateUser(
        @PathVariable(value = "userId", required = false) final String userId,
        @NonNull @RequestBody User user
    ) throws URISyntaxException {
        log.debug("REST request to partial update Worker partially : {}, {}", userId, user);
        if (user.getUserId() == null) {
            throw new BadRequestException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(userId, user.getUserId())) {
            throw new BadRequestException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userRepository.existsById(userId)) {
            throw new BadRequestException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserDto> result = userService.partialUpdateUser(user);

        return ResponseEntity.ok(Optional.of(result.get()));
    }

    /**
     * {@code GET  /users} : get all the users.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of users in body.
     */
    @GetMapping("")
    @Cacheable(value = "users", key = "'users'")
    public List<User> getAllUsers() {
        log.debug("REST request to get all users");
        return userService.findAllUsers();
    }

    /**
     * {@code GET  /users/:id} : get the "id" user.
     *
     * @param id the id of the user to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the user, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") String id) {
        log.debug("REST request to get user : {}", id);
        Optional<UserDto> userOptional = userService.findOneUser(id);

        if (userOptional.isPresent()) {
            log.debug("user found: {}", userOptional.get());
            return ResponseEntity.ok(userOptional.get());
        } else {
            log.debug("user not found for id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * {@code DELETE  /users/:id} : delete the "id" user.
     *
     * @param id the id of the user to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") String id) {
        log.debug("REST request to delete user : {}", id);
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body("deleted user");
    }

    @GetMapping("/clear_cache")
    @CacheEvict(value = "users", allEntries = true )
    public String clearCache(){
        return "Cache has been cleared";
    }
}
