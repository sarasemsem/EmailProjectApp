package com.emailProcessor.emailProcessor.service;
import java.util.List;
import com.emailProcessor.basedomains.dto.EmailDto;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
        List<EmailDto> getAllEmails();

    EmailDto getEmailById(String id);

    ResponseEntity<String> createEmail(EmailDto emailDto);



    EmailDto updateEmail(String id, EmailDto emailDto);

    void deleteEmail(String id);
}
