package com.emailProcessor.emailProcessor.service;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.emailProcessor.basedomains.dto.ActionParamDto;
import com.emailProcessor.basedomains.dto.EmailDto;
import com.emailProcessor.emailProcessor.entity.Email;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    List<EmailDto> getAllEmails();
    List<EmailDto> getUrgentEmails();
    Email getEmailById(String id) throws InterruptedException;
    Email createEmail(EmailDto emailDto);
    Optional<Email> partialUpdate(EmailDto email);
    List<EmailDto> getAllUntreatedEmails();
    List<EmailDto> getTreatedEmails();
    EmailDto updateEmail(String id, EmailDto emailDto);
    ResponseEntity<String> deleteEmails(String[] ids);
    ResponseEntity<String> deleteEmail(String id);
    EmailDto convertToDto(Email email);
    List<ActionParamDto> getTodaysDeliveredActions();
    List<ActionParamDto> getThisMonthDeliveredActions();
    List<ActionParamDto> getThisWeekDeliveredActions();
    Long countEmails();
    double calculateSuccessRate();
    Map<String, Long> getTopCategories();
    List<EmailDto> getfiltredEmails(int page, int size);
    List<EmailDto> getAllUnaffectedEmails(int page, int size);
}
