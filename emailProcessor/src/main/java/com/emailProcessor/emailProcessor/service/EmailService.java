package com.emailProcessor.emailProcessor.service;
import java.util.List;
import java.util.Optional;

import com.emailProcessor.basedomains.dto.EmailDto;
import com.emailProcessor.emailProcessor.entity.Email;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    List<EmailDto> getAllEmails();
    List<EmailDto> getUrgentEmails();
    EmailDto getEmailById(String id);
    Email createEmail(EmailDto emailDto);
    Optional<Email> partialUpdate(EmailDto email);
    List<EmailDto> getAllUntreatedEmails();
    List<EmailDto> getTreatedEmails();
    EmailDto updateEmail(String id, EmailDto emailDto);
    void deleteEmails(String[] ids);
}
