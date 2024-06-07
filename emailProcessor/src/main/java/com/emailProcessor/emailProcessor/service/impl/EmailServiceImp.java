package com.emailProcessor.emailProcessor.service.impl;

import com.emailProcessor.basedomains.dto.*;
import com.emailProcessor.emailProcessor.entity.*;
import com.emailProcessor.emailProcessor.repository.EmailProcessingResultRepository;
import com.emailProcessor.emailProcessor.repository.EmailRepository;
import com.emailProcessor.emailProcessor.repository.RelatedDataRepository;
import com.emailProcessor.emailProcessor.service.EmailService;
import com.emailProcessor.emailProcessor.service.SenderService;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.modelmapper.MappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
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
    @Autowired
    private SenderService senderService;
    private final ModelMapper modelMapper;
    @Autowired
    private ModelMapper customModelMapper;

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImp.class);
    @Autowired
    private final EmailProcessingResultRepository emailProcessingResultRepository;
    @Autowired
    private final RelatedDataRepository relatedDataRepository;

    @Override
    @Cacheable(value = "emails", key = "'allEmails'")
    public List<EmailDto> getAllEmails() {
        try {
            int batchSize = 50; // Adjust the batch size as per your requirement
            int page = 0;
            List<EmailDto> allEmails = new ArrayList<>();
            List<Email> emails;
            do {
                emails = emailRepository.findAllEmails(PageRequest.of(page, batchSize));
                allEmails.addAll(emails.stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList()));
                page++;
            } while (!emails.isEmpty());
            return allEmails;
        } catch (Exception e) {
            logger.error("Error occurred while retrieving emails: {}", e.getMessage());
            return Collections.emptyList(); // Return an empty list if an error occurs
        }
    }

    private EmailDto convertToDto(Email email) {
        if (email == null) {
            return null; // or throw an exception, depending on your requirements
        }
        EmailDto dto = new EmailDto();
        dto.setEmailId(email.getEmailId());
        if (email.getSender() != null) {
            dto.setSender(email.getSender());
        }
        if (email.getSubject() != null) {
            dto.setSubject(email.getSubject());
        }
        if (email.getContent() != null) {
            dto.setContent(email.getContent());
        }
        if (email.getOriginalContent() != null) {
            dto.setOriginalContent(email.getOriginalContent());
        }
        if (email.getDate() != null) {
            dto.setDate(email.getDate());
        }
        dto.setIsRead(email.getIsRead());
        dto.setTreated(email.getTreated());
        dto.setUrgent(email.getUrgent());
        dto.setImportant(email.getImportant());
        dto.setDraft(email.getDraft());
        dto.setSpam(email.getSpam());
        dto.setArchived(email.getArchived());

            try {
                logger.debug("Successfully mapped Email to EmailDto: {}", dto);
                // Null check before mapping EmailProcessingResult to EmailProcessingResultDto
                if (email.getResult() != null) {
                    EmailProcessingResult result = email.getResult();
                    EmailProcessingResultDto resultDto = new EmailProcessingResultDto();

                    if (result.getProposedCategories() != null) {
                        resultDto.setProposedCategories(customModelMapper.map(result.getProposedCategories(), new TypeToken<List<CategoryDto>>() {}.getType()));
                    }
                    if (result.getSelectedCategories() != null) {
                        resultDto.setSelectedCategories(customModelMapper.map(result.getSelectedCategories(), new TypeToken<List<CategoryDto>>() {}.getType()));
                    }
                    if (result.getFoundKeywords() != null) {
                        resultDto.setFoundKeywords(customModelMapper.map(result.getFoundKeywords(), new TypeToken<List<KeywordDto>>() {}.getType()));
                    }
                    if (result.getScore() != null) {
                        resultDto.setScore(result.getScore());
                    }
                    if (result.getRelatedActions() != null) {
                        resultDto.setRelatedActions(customModelMapper.map(result.getRelatedActions(), new TypeToken<List<ActionParamDto>>() {}.getType()));
                    }

                    dto.setResult(resultDto);
                }
            } catch (MappingException e) {
                logger.error("Error mapping Email to EmailDto", e);
                throw e;
            }

        // Null check before mapping Sender to SenderDto
        if (email.getContact() != null) {
            dto.setContact(modelMapper.map(email.getContact(), SenderDto.class));
        }
        if (email.getRelatedData() != null) {
            dto.setRelatedData(modelMapper.map(email.getRelatedData(), RelatedDataDto.class));
        }
        return dto;
    }

    @Override
    public EmailDto getEmailById(String id) throws InterruptedException {
        Email email = emailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found with id: " + id));
        return convertToDto(email);
    }

    @Override
    @CachePut(value = "emails", key = "'allEmails'")
    public Email createEmail(EmailDto emailDto) {
        Optional<Sender> sender = senderService.findByEmail(emailDto.getSender());
        sender.ifPresent(value -> emailDto.setContact(modelMapper.map(value, SenderDto.class)));
        Email email = modelMapper.map(emailDto, Email.class);
        return emailRepository.save(email);
    }

    @Override
    public Optional<Email> partialUpdate(EmailDto emailDto) {
        logger.debug("Request to partially update Email : {}", emailDto);

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
                    if (emailDto.getOriginalContent() != null) {
                        existingEmail.setOriginalContent(emailDto.getOriginalContent());
                    }
                    if (emailDto.getDate() != null) {
                        existingEmail.setDate(emailDto.getDate());
                    }
                    if (emailDto.getResult() != null) {
                        EmailProcessingResultDto resultDto = emailDto.getResult();
                        EmailProcessingResult result = modelMapper.map(resultDto, EmailProcessingResult.class);
                        existingEmail.setResult(result);
                    }
                    if (emailDto.getIsRead() != null) {
                        existingEmail.setIsRead(emailDto.getIsRead());
                    }
                    if (emailDto.getTreated() != null) {
                        existingEmail.setTreated(emailDto.getTreated());
                    }
                    if (emailDto.getUrgent() != null) {
                        existingEmail.setUrgent(emailDto.getUrgent());
                    }
                    if (emailDto.getImportant() != null) {
                        existingEmail.setImportant(emailDto.getImportant());
                    }
                    if (emailDto.getDraft() != null) {
                        existingEmail.setDraft(emailDto.getDraft());
                    }
                    if (emailDto.getSpam() != null) {
                        existingEmail.setSpam(emailDto.getSpam());
                    }
                    if (emailDto.getArchived() != null) {
                        existingEmail.setArchived(emailDto.getArchived());
                    }
                    if (emailDto.getRelatedData() != null) {
                        RelatedDataDto relatedDataDto = emailDto.getRelatedData();
                        RelatedData data = modelMapper.map(relatedDataDto, RelatedData.class);
                        existingEmail.setRelatedData(data);
                    }
                    if (emailDto.getContact() != null) {
                        SenderDto senderDto = emailDto.getContact();
                        Sender sender = modelMapper.map(senderDto, Sender.class);
                        existingEmail.setContact(sender);
                    }

                    // Save the email in the repository
                    Email savedEmail = emailRepository.save(existingEmail);

                    // Update cached email list after saving the email
                    updateCachedEmailList(emailDto);

                    return savedEmail;
                });
    }

    @Override
    public List<EmailDto> getTreatedEmails() {
        List<Email> emails = emailRepository.findByTreatedTrue();
        return emails.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmailDto> getAllUntreatedEmails() {
        List<Email> emails = emailRepository.findByTreatedFalse();
        return emails.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmailDto> getUrgentEmails() {
        List<Email> emails = emailRepository.findByUrgentTrue();
        return emails.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmailDto updateEmail(String id, EmailDto emailDto) {
        Email existingEmail = emailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("email not found with id: " + id));
        // Update fields
        existingEmail.setSubject(emailDto.getSubject());
        existingEmail.setOriginalContent(emailDto.getOriginalContent());
        existingEmail.setContent(emailDto.getContent());
        existingEmail.setSender(emailDto.getSender());
        existingEmail.setIsRead(emailDto.getIsRead());
        existingEmail.setTreated(emailDto.getTreated());
        existingEmail.setUrgent(emailDto.getUrgent());
        existingEmail.setImportant(emailDto.getImportant());
        existingEmail.setDraft(emailDto.getDraft());
        existingEmail.setSpam(emailDto.getSpam());
        existingEmail.setArchived(emailDto.getArchived());
        Email updatedEmail = emailRepository.save(existingEmail);
        updateCachedEmailList(emailDto);
        return convertToDto(updatedEmail);
    }

    @CachePut(value = "emails", key = "'allEmails'")
    private List<EmailDto> updateCachedEmailList(EmailDto emailDto) {
        List<EmailDto> cachedEmails = getAllEmails();
        List<EmailDto> updatedEmails = new ArrayList<>(cachedEmails);

        boolean emailUpdated = false;
        for (int i = 0; i < updatedEmails.size(); i++) {
            if (updatedEmails.get(i).getEmailId().equals(emailDto.getEmailId())) {
                updatedEmails.set(i, emailDto);
                emailUpdated = true;
                break;
            }
        }

        if (!emailUpdated) {
            updatedEmails.add(emailDto);
        }

        return updatedEmails;
    }

    @Override
    public void deleteEmails(String[] ids) {
        for (String id : ids) {
            Email email = emailRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Email not found with id: " + id));

            EmailProcessingResult result = email.getResult();
            if (result != null) {
                emailProcessingResultRepository.delete(result);
            }

            RelatedData relatedData = email.getRelatedData();
            if (relatedData != null) {
                relatedDataRepository.delete(relatedData);
            }

            emailRepository.delete(email);
        }
    }
}
