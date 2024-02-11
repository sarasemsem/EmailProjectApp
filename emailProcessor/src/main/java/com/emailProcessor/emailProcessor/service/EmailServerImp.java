package com.emailProcessor.emailProcessor.service;

import com.emailProcessor.basedomains.dto.EmailDto;
import com.emailProcessor.emailProcessor.entity.Email;
import com.emailProcessor.emailProcessor.repository.EmailRepository;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmailServerImp implements EmailService{
    @Autowired
    private EmailRepository emailRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<EmailDto> getAllEmails() {
        List<Email> users = emailRepository.findAll();
        return users.stream()
                .map(user -> modelMapper.map(user, EmailDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public EmailDto getEmailById(String id) {
        Email user = emailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return modelMapper.map(user, EmailDto.class);
    }

    @Override
    public ResponseEntity<String> createEmail(EmailDto emailDto) {
        Email email = modelMapper.map(emailDto, Email.class);
        Email savedEmail = emailRepository.save(email);
        if (savedEmail != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Email inserted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to insert the Email");
        }
    }

    @Override
    public EmailDto updateEmail(String id, EmailDto emailDto) {
        Email existingEmail = emailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Update fields
        existingEmail.setSubject(emailDto.getSubject());
        existingEmail.setContent(emailDto.getContent());
        existingEmail.setSender(emailDto.getSender());

        Email updatedUser = emailRepository.save(existingEmail);
        return modelMapper.map(updatedUser, EmailDto.class);
    }

    @Override
    public void deleteEmail(String id) {
        if (!emailRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        emailRepository.deleteById(id);
    }
}
