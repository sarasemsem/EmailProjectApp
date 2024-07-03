package com.emailProcessor.emailProcessor.controller;

import com.emailProcessor.basedomains.dto.CustomResponse;
import com.emailProcessor.basedomains.dto.EmailDto;
import com.emailProcessor.basedomains.dto.EmailProcessingResultDto;
import com.emailProcessor.emailProcessor.entity.Email;
import com.emailProcessor.emailProcessor.entity.EmailProcessingResult;
import com.emailProcessor.emailProcessor.entity.Sender;
import com.emailProcessor.emailProcessor.service.EmailProcessingResultService;
import com.emailProcessor.emailProcessor.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * REST controller for managing {@link EmailProcessingResultDto}.
 */
@RestController
@RequestMapping("api/v1/result")
@RequiredArgsConstructor
public class EmailProcessingResultController {

    private final Logger log = LoggerFactory.getLogger(EmailProcessingResultController.class);

    private static final String ENTITY_NAME = "result";


    private final ModelMapper modelMapper;
    @Autowired
    private final EmailProcessingResultService emailProcessingResultService;
    @Autowired
    private final EmailService emailService;



    @GetMapping("/{id}")
    public ResponseEntity<EmailProcessingResult> getResult(@PathVariable String id) {
        log.debug("REST request to get result : {}", id);
        EmailProcessingResult processingResult = emailProcessingResultService.findById(id);

        if (processingResult != null) {
            System.out.println("processing result is : "+processingResult);
            return ResponseEntity.ok(processingResult);
        } else {
            log.debug("Sender not found for id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * {@code POST  /result} : Create a new EmailResult.
     *
     * @param resultDto the result to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new action, or with status {@code 400 (Bad Request)} if the action has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CustomResponse> createResult(@Validated @PathVariable String emailId ,@RequestBody EmailProcessingResultDto resultDto) throws Exception {
        log.debug("REST request to save result : {}", resultDto);
        if (resultDto.getId() != null) {
            throw new Exception("A new result cannot already have an ID");
        }
        try {
            EmailProcessingResultDto result = emailProcessingResultService.saveEmailProcessingResult(resultDto);

            // Update category with the newly saved result
            if (result.getId()!= null) {
                Email email = emailService.getEmailById(emailId);
                EmailDto emailDto = emailService.convertToDto(email);
                emailDto.setResult(result);
                emailDto.setTreated(true);
                emailService.partialUpdate(emailDto);
            }
            CustomResponse customResponse = new CustomResponse(result, HttpStatus.CREATED.value(), "result saved successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(customResponse);
        }catch (Exception e) {
            log.error("Error saving result", e);
            CustomResponse customResponse = new CustomResponse(resultDto, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error saving result");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(customResponse);
        }
    }

}
