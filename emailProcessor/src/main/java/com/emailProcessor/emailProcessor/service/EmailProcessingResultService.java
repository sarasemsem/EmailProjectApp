package com.emailProcessor.emailProcessor.service;

import com.emailProcessor.basedomains.dto.EmailProcessingResultDto;
import org.springframework.stereotype.Service;

@Service
public interface EmailProcessingResultService {
    EmailProcessingResultDto saveEmailProcessingResult(EmailProcessingResultDto result);
}