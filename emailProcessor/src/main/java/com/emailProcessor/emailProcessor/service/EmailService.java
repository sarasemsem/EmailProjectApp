package com.emailProcessor.emailProcessor.service;
import java.util.List;
import com.emailProcessor.basedomains.dto.EmailDto;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
        List<EmailDto> getAllEmails();

    EmailDto getEmailById(ObjectId id);

    ResponseEntity<String> createEmail(EmailDto emailDto);

    EmailDto updateEmail(ObjectId id, EmailDto emailDto);

        void deleteEmail(ObjectId id);

}
