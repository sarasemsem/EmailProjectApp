package com.emailProcessor.emailProcessor.service;

import com.emailProcessor.basedomains.dto.EmailProcessingResultDto;
import com.emailProcessor.emailProcessor.entity.EmailProcessingResult;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface EmailProcessingResultService {
    EmailProcessingResultDto saveEmailProcessingResult(EmailProcessingResultDto result) throws Exception;
    EmailProcessingResult findById(String id);
    Optional<EmailProcessingResult> partialUpdate(EmailProcessingResult processingResult);
    EmailProcessingResultDto convertEmailProcessorToDto(EmailProcessingResult processingResult);
    EmailProcessingResult convertToEntity(EmailProcessingResultDto dto);
    ResponseEntity<String> deleteEmailProcessingResult(String id);
}
