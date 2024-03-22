package com.emailProcessor.emailProcessor.service.impl;

import com.emailProcessor.basedomains.dto.EmailDto;
import com.emailProcessor.basedomains.dto.EmailProcessingResultDto;
import com.emailProcessor.emailProcessor.entity.Email;
import com.emailProcessor.emailProcessor.entity.EmailProcessingResult;
import com.emailProcessor.emailProcessor.repository.EmailRepository;
import com.emailProcessor.emailProcessor.service.EmailService;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmailServiceImp implements EmailService {
    @Autowired
    private EmailRepository emailRepository;

    private final ModelMapper modelMapper;
    private final Logger log = LoggerFactory.getLogger(EmailServiceImp.class);


    @Override
    public List<EmailDto> getAllEmails() {
        List<Email> emails = emailRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
        return emails.stream()
                .map(email -> modelMapper.map(email, EmailDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public EmailDto getEmailById(String id) {
        Email user = emailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found with id: " + id));
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
    public Optional<Email> partialUpdate(EmailDto emailDto) {
        log.debug("Request to partially update Email : {}", emailDto);
        System.out.println("emails : " + emailDto);
        return emailRepository
                .findById(emailDto.getEmailId())
                .map(existingEmail -> {
                    if (emailDto.getSender() != null) {
                        existingEmail.setSender(emailDto.getSender());
                    }
                    if (emailDto.getSubject() != null) {
                        existingEmail.setSubject(emailDto.getSubject());
                    }
                    if (emailDto.getContent() != null) {
                        existingEmail.setContent(emailDto.getContent());
                    }
                    if (emailDto.getDate() != null) {
                        existingEmail.setDate(emailDto.getDate());
                    }
                    if (emailDto.getResult() != null) {
                        EmailProcessingResultDto resultDto = emailDto.getResult();
                        EmailProcessingResult result = modelMapper.map(resultDto, EmailProcessingResult.class);
                        existingEmail.setResult(result);
                    }
                    if (emailDto.getTreated() != null) {
                        existingEmail.setTreated(emailDto.getTreated());
                    }
                    return existingEmail;
                })
                .map(emailRepository::save);
    }


    @Override
    public List<EmailDto> getAllUntreatedEmails() {
        List<Email> emails = emailRepository.findByTreatedFalse();
        return emails.stream()
                .map(email -> modelMapper.map(email, EmailDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public EmailDto updateEmail(String id, EmailDto emailDto) {
        Email existingEmail = emailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("email not found with id: " + id));
        // Update fields
        existingEmail.setSubject(emailDto.getSubject());
        existingEmail.setOriginalContent(emailDto.getOriginalContent());
        existingEmail.setContent(emailDto.getContent());
        existingEmail.setSender(emailDto.getSender());
        existingEmail.setIsRead(emailDto.getIsRead());
        Email updatedUser = emailRepository.save(existingEmail);
        return modelMapper.map(updatedUser, EmailDto.class);
    }

    @Override
    public void deleteEmail(String id) {
        if (!emailRepository.existsById(id)) {
            throw new ResourceNotFoundException("Email not found with id: " + id);
        }
        emailRepository.deleteById(id);
    }
}
