package com.emailProcessor.emailProcessor.service.impl;

import com.emailProcessor.basedomains.dto.CredentialsDto;
import com.emailProcessor.basedomains.dto.User1Dto;
import com.emailProcessor.basedomains.dto.UserDto;
import com.emailProcessor.emailProcessor.controller.errors.AppExecption;
import com.emailProcessor.emailProcessor.entity.User;
import com.emailProcessor.emailProcessor.repository.UserRepository;
import com.emailProcessor.emailProcessor.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImp implements UserService {
    private final Logger log = LoggerFactory.getLogger(SenderServiceImp.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    @Override
    public UserDto login(CredentialsDto credentialsDto) {
        System.out.println("credentials are :"+credentialsDto);
        User user = userRepository.findByEmail(credentialsDto.email())
                .orElseThrow(() -> {
                    return new AppExecption("Unknown User",HttpStatus.NOT_FOUND);
                });
        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.password()),
                user.getPassword())) {
            return modelMapper.map(user, UserDto.class);
        }
        throw new AppExecption("Invalid password",HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<String> saveUser(User1Dto user) {
        log.debug("Request to save user: {}", user);
        try {
            Optional<User> findUser = userRepository.findByEmail(user.getEmail());
            if (findUser.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
            }
            User toSaveUser = modelMapper.map(user, User.class);
            toSaveUser.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(toSaveUser);
            if (savedUser.getUserId() != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body("User saved successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to insert User");
            }
        } catch (DataIntegrityViolationException ex) {
            log.error("Error saving User", ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error saving User", ex);
        }
    }

    @Override
    public UserDto updateUser(User user) {
        log.debug("Request to update user : {}", user);
        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserDto.class);
    }

    @Override
    public Optional<UserDto> partialUpdateUser(User user) {
        log.debug("Request to partially update User : {}", user);
        return userRepository
                .findById(user.getUserId())
                .map(existingUser -> {
                    if (user.getFirstName() != null) {
                        existingUser.setFirstName(user.getFirstName());
                    }
                    if (user.getEmail() != null) {
                        existingUser.setEmail(user.getEmail());
                    }
                    if (user.getRoles() != null) {
                        // Assuming that getRoles() returns a Set<SecurityRole>
                        existingUser.getRoles().clear(); // Clear existing roles
                        existingUser.getRoles().addAll(user.getRoles());
                    }
                    if (user.getPhoneNbr() != null) {
                        existingUser.setPhoneNbr(user.getPhoneNbr());
                    }
                    if (user.getPassword() != null) {
                        existingUser.setPassword(user.getPassword());
                    }

                    return existingUser;
                })
                .map(userRepository::save)
                .map(updatedUser -> modelMapper.map(updatedUser, UserDto.class));
    }


    @Override
    public List<User> findAllUsers() {
        log.debug("Request to get all Users");
        return userRepository.findAll();
    }
    @Override
    public UserDto findByLogin(String login) {
        Optional<User> user = userRepository.findByEmail(login);
        return modelMapper.map(user, UserDto.class);
    }
    @Override
    public Optional<UserDto> findOneUser(String id) {
        log.debug("Request to get Sender : {}", id);
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserDto userDto = modelMapper.map(user, UserDto.class);
            return Optional.of(userDto);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void deleteUser(String id) {
        log.debug("Request to delete user : {}", id);
        userRepository.deleteById(id);
    }
}
