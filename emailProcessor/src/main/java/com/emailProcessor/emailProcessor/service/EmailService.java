package com.emailProcessor.emailProcessor.service;
import java.util.List;
import java.util.Optional;

import com.emailProcessor.basedomains.dto.EmailDto;
import com.emailProcessor.emailProcessor.entity.Email;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    List<EmailDto> getAllEmails();
    EmailDto getEmailById(String id);
    ResponseEntity<String> createEmail(EmailDto emailDto);
    Optional<Email> partialUpdate(EmailDto email);
    List<EmailDto> getAllUntreatedEmails();
    EmailDto updateEmail(String id, EmailDto emailDto);
    void deleteEmail(String id);
}
