package com.emailProcessor.emailProcessor.service.impl;

import com.emailProcessor.basedomains.dto.EmailProcessingResultDto;
import com.emailProcessor.emailProcessor.entity.EmailProcessingResult;
import com.emailProcessor.emailProcessor.repository.EmailProcessingResultRepository;
import com.emailProcessor.emailProcessor.service.EmailProcessingResultService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional
@AllArgsConstructor
public class EmailProcessingResultImp implements EmailProcessingResultService {
    private final Logger log = LoggerFactory.getLogger(EmailProcessingResultImp.class);
    private final EmailProcessingResultRepository emailProcessingResultRepository;
    private final ModelMapper modelMapper;
    @Override
    public EmailProcessingResultDto saveEmailProcessingResult(EmailProcessingResultDto resultDto) {
        log.debug("Request to save the result : {}", resultDto);
        System.out.println("Request to save the result :" + resultDto.toString());
        EmailProcessingResult result = modelMapper.map(resultDto, EmailProcessingResult.class);
        EmailProcessingResult savedResult = emailProcessingResultRepository.save(result);
        // Save the result entity
        return modelMapper.map(savedResult, EmailProcessingResultDto.class);
    }
}
