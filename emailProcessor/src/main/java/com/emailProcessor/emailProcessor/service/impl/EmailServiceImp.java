package com.emailProcessor.emailProcessor.service.impl;

import com.emailProcessor.basedomains.dto.*;
import com.emailProcessor.emailProcessor.entity.Email;
import com.emailProcessor.emailProcessor.entity.EmailProcessingResult;
import com.emailProcessor.emailProcessor.repository.EmailProcessingResultRepository;
import com.emailProcessor.emailProcessor.repository.EmailRepository;
import com.emailProcessor.emailProcessor.service.EmailService;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmailServiceImp implements EmailService {
    @Autowired
    private EmailRepository emailRepository;

    private final ModelMapper modelMapper;
    private final Logger log = LoggerFactory.getLogger(EmailServiceImp.class);
    private final EmailProcessingResultRepository emailProcessingResultRepository;

    @Override
    @Cacheable(value = "emails", key = "'allEmails'")
    public List<EmailDto> getAllEmails() {
        List<Email> emails = emailRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
        return emails.stream()
                .map(this::convertToDto)
                .toList();
}

    private EmailDto convertToDto(Email email) {
        if (email == null) {
            return null; // or throw an exception, depending on your requirements
        }
        EmailDto dto = new EmailDto();
        dto.setEmailId(email.getEmailId());
        dto.setSender(email.getSender());
        dto.setSubject(email.getSubject());
        dto.setContent(email.getContent());
        dto.setOriginalContent(email.getOriginalContent());
        dto.setIsRead(email.getIsRead());
        dto.setDate(email.getDate());

        // Null check before mapping EmailProcessingResult to EmailProcessingResultDto
        if (email.getResult() != null) {
            EmailProcessingResult result = email.getResult();
            EmailProcessingResultDto resultDto = new EmailProcessingResultDto();
            // Map lists of categories and keywords if they are not null
            if (result.getProposedCategories() != null) {
                resultDto.setProposedCategories(modelMapper.map(result.getProposedCategories(), new TypeToken<List<CategoryDto>>() {}.getType()));
            }
            if (result.getSelectedCategories() != null) {
                resultDto.setSelectedCategories(modelMapper.map(result.getSelectedCategories(), new TypeToken<List<CategoryDto>>() {}.getType()));
            }
            if (result.getFoundKeywords() != null) {
                resultDto.setFoundKeywords(modelMapper.map(result.getFoundKeywords(), new TypeToken<List<KeywordDto>>() {}.getType()));
            }
            // Set the mapped EmailProcessingResultDto to the EmailDto
            dto.setResult(resultDto);
        }

        dto.setTreated(email.getTreated());
        // Null check before mapping Sender to SenderDto
        if (email.getContact() != null) {
            dto.setContact(modelMapper.map(email.getContact(), SenderDto.class));
        }
        return dto;
    }

    @Override
    public EmailDto getEmailById(String id) {
        Email user = emailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found with id: " + id));
        return modelMapper.map(user, EmailDto.class);
    }

    @Override
    @CachePut(value = "emails", key = "'allEmails'")
    public Email createEmail(EmailDto emailDto) {
        Email email = modelMapper.map(emailDto, Email.class);
        return emailRepository.save(email);
    }

    @Override
    @CachePut(value = "emails", key = "'allEmails'")
    public Optional<Email> partialUpdate(EmailDto emailDto) {
        log.debug("Request to partially update Email : {}", emailDto);
        System.out.println("emails : " + emailDto);
        updateCachedEmailList(emailDto);
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
    @CachePut(value = "emails", key = "'allEmails'")
    public EmailDto updateEmail(String id, EmailDto emailDto) {
        Email existingEmail = emailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("email not found with id: " + id));
        // Update fields
        existingEmail.setSubject(emailDto.getSubject());
        existingEmail.setOriginalContent(emailDto.getOriginalContent());
        existingEmail.setContent(emailDto.getContent());
        existingEmail.setSender(emailDto.getSender());
        existingEmail.setIsRead(emailDto.getIsRead());
        Email updatedEmail = emailRepository.save(existingEmail);
        updateCachedEmailList(emailDto);
        return modelMapper.map(updatedEmail, EmailDto.class);
    }
    // Helper method to update the cached list when an email is saved/updated
    @CachePut(value = "emails", key = "'allEmails'")
    private List<EmailDto> updateCachedEmailList(EmailDto emailDto) {
        List<EmailDto> cachedEmails = getAllEmails();
        // Find the email with the same ID as the saved/updated email
        for (int i = 0; i < cachedEmails.size(); i++) {
            if (cachedEmails.get(i).getEmailId().equals(emailDto.getEmailId())) {
                // Update the email in the cached list
                cachedEmails.set(i, emailDto);
                break;
            }
        }
        return cachedEmails ;
    }
    @Override
    public void deleteEmails(String[] ids) {
        for (String id : ids) {
            // Find the email by ID
            Email email = emailRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Email not found with id: " + id));

            // Check if the email has an associated EmailProcessingResult
            EmailProcessingResult result = email.getResult();
            if (result != null) {
                // Delete the associated EmailProcessingResult
                emailProcessingResultRepository.delete(result);
            }

            // Delete the email
            emailRepository.delete(email);
        }
    }
}
