package com.emailProcessor.emailProcessor.service.impl;

import com.emailProcessor.basedomains.dto.*;
import com.emailProcessor.emailProcessor.entity.*;
import com.emailProcessor.emailProcessor.repository.ActionParamsRepository;
import com.emailProcessor.emailProcessor.repository.EmailProcessingResultRepository;
import com.emailProcessor.emailProcessor.repository.EmailRepository;
import com.emailProcessor.emailProcessor.repository.RelatedDataRepository;
import com.emailProcessor.emailProcessor.service.*;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.bson.types.ObjectId;
import org.modelmapper.MappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmailServiceImp implements EmailService {

    private EmailRepository emailRepository;
    private SenderService senderService;
    @Autowired
    public EmailProcessingResultService resultService ;
    @Autowired
    public ActionParamService actionParamService ;

    private final ModelMapper modelMapper;
    private FileService fileService;
    private AttachmentService attachmentService;
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImp.class);
    private final EmailProcessingResultRepository emailProcessingResultRepository;
    private final RelatedDataRepository relatedDataRepository;
    private final ActionParamsRepository actionParamsRepository;

    //@Cacheable(value = "emails", key = "'allEmails'")
    @Override
    public List<EmailDto> getAllEmails() {
        logger.debug("in getAllEmails");
        try {
            int batchSize = 50;
            int page = 0;
            List<EmailDto> allEmails = new ArrayList<>();
            List<Email> emails;
            do {
                emails = emailRepository.findAllEmails(PageRequest.of(page, batchSize));
                logger.debug("Fetched batch of emails, size: {}", emails.size());
                allEmails.addAll(emails.stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList()));
                page++;
            } while (!emails.isEmpty());
            logger.debug("Exiting getAllEmails with {} emails", allEmails.size());
            return allEmails;
        } catch (Exception e) {
            logger.error("Error occurred while retrieving emails", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<EmailDto> getfiltredEmails(int page, int size) {
        logger.debug("in get filtred Emails");
        try {
            Pageable pageable = PageRequest.of(page - 1, size); // PageRequest starts from 0
            List<Email> emails = emailRepository.findAllEmail(pageable);
            logger.debug("Fetched batch of emails, size: {}", emails.size());
            return emails.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error occurred while retrieving emails", e);
            return Collections.emptyList();
        }
    }
    @Override
    public double calculateSuccessRate() {
        long totalEmails = emailRepository.count();
        long failedEmails = getAllUntreatedEmails().size();
        if (totalEmails == 0) {
            return 0;
        }
        return ((double) (totalEmails - failedEmails) / totalEmails) * 100;
    }
    @Override
    public Map<String, Long> getTopCategories() {

        List<Email> emails = emailRepository.findAll();
        System.out.println("top categories emails :"+ emails);
        Map<String, Long> categoryCounts = emails.stream()
                .filter(email -> email.getResult() != null) // Ensure the result is not null
                .flatMap(email -> email.getResult().getSelectedCategories().stream())
                .collect(Collectors.groupingBy(Category::getTitle, Collectors.counting()));

        return categoryCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }
    @Override
    public EmailDto convertToDto(Email email) {
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
        // Null check before mapping Sender to SenderDto
        if (email.getContact() != null) {
            dto.setContact(modelMapper.map(email.getContact(), SenderDto.class));
        }
        if (email.getRelatedData() != null) {
            dto.setRelatedData(modelMapper.map(email.getRelatedData(), RelatedDataDto.class));
        }
        if (email.getAttachments() != null) {
            List<AttachmentDto> attachmentList = new ArrayList<>();
            email.getAttachments().stream().map(file -> {
                if (file != null && file.getFileId() != null) {
                    GridFsResource id = fileService.getFile(file.getFileId());
                    AttachmentDto attachmentDto = new AttachmentDto();
                    attachmentDto.setFileName(file.getFileName());
                    attachmentDto.setFileId(id.toString());

                    attachmentList.add(attachmentDto);
                    return id.toString();
                }
                return null;
            }).collect(Collectors.toList());
            dto.setAttachments(attachmentList);
        }
            try {
                logger.debug("Successfully mapped Email to EmailDto: {}", dto);
                // Null check before mapping EmailProcessingResult to EmailProcessingResultDto
                if (email.getResult() != null) {
                    EmailProcessingResult result = email.getResult();
                    EmailProcessingResultDto resultDto = resultService.
                            convertEmailProcessorToDto(result);
                    System.out.println("this is get email result :"+resultDto.getRelatedActions());
                    dto.setResult(resultDto);
                }
            } catch (MappingException e) {
                logger.error("Error mapping Email to EmailDto", e);
                throw e;
            }

        return dto;
    }

   /* @Override
    public EmailDto getEmailById(String id) throws InterruptedException {
        Email email = emailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found with id: " + id));
        return convertToDto(email);
    }*/

    @Override
    public Email getEmailById(String id) throws InterruptedException {
        Email email = emailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found with id: " + id));
        return email;
    }
    //@CachePut(value = "emails", key = "'allEmails'")
    @Override
    public Email createEmail(EmailDto emailDto) {
        Email email = modelMapper.map(emailDto, Email.class);
        Optional<Sender> sender = senderService.findByEmail(emailDto.getSender());
        sender.ifPresent(value -> email.setContact(value));
        // Convert and set attachments
        if (emailDto.getAttachments() != null) {
            List<Attachment> attachmentList =new ArrayList<>();
            List<String> attachmentIds = emailDto.getAttachments().stream().map(file -> {
                try {
                    ObjectId id = fileService.saveFile(file);
                    Attachment attachment= new Attachment();
                    attachment.setFileName(file.getFileName());
                    attachment.setFileId(id.toString());

                    //save the attachment in DB
                    Attachment savedAttachment= attachmentService.saveAttachment(attachment);

                    attachmentList.add(savedAttachment);
                    return id.toString();
                } catch (IOException e) {
                    // Handle exception
                    e.printStackTrace();
                    logger.error("Error saving attachment", e);
                    return null;
                }
            }).toList();

            //email.setAttachmentIds(attachmentIds);
            email.setAttachments(attachmentList);
        }
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
                        EmailProcessingResult result = resultService.convertToEntity(resultDto);
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
    public Long countEmails() {
        return emailRepository.count();
    }
    @Override
    public List<ActionParamDto> getTodaysDeliveredActions() {
        System.out.println("getTodaysDeliveredActions");
        Instant startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        List<ActionParam> actionParams = actionParamsRepository.
                findActionParamWithAffectedTrueAndActionDateBetween(startOfDay, endOfDay);
        System.out.println("emails :" +actionParams);
        return actionParams.stream()
                .map(param -> actionParamService.toActionParamDto(param))
                .collect(Collectors.toList());
    }
    @Override
    public List<ActionParamDto> getThisMonthDeliveredActions() {
        System.out.println("getthisMonthDeliveredActions");
        // Get the start of the current month
        Instant startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        // Get the start of the next month
        Instant startOfNextMonth = LocalDate.now().plusMonths(1).withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        List<ActionParam> actionParams = actionParamsRepository.
                findActionParamWithAffectedTrueAndActionDateBetween(startOfMonth, startOfNextMonth);
        System.out.println("emails :" + actionParams.size());
        return actionParams.stream()
                .map(param -> actionParamService.toActionParamDto(param))
                .collect(Collectors.toList());
    }
    @Override
    public List<ActionParamDto> getThisWeekDeliveredActions() {
        System.out.println("get this Week Delivered Actions");
        // Get the start of the current week (Monday)
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);  // Sunday

        // Convert to Instant
        Instant startOfWeekInstant = startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfWeekInstant = endOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant();

        List<ActionParam> actionParams = actionParamsRepository.
                findActionParamWithAffectedTrueAndActionDateBetween(startOfWeekInstant, endOfWeekInstant);
        System.out.println("emails :" + actionParams.size());
        return actionParams.stream()
                .map(param -> actionParamService.toActionParamDto(param))
                .collect(Collectors.toList());
    }

    @Override
    public List<EmailDto> getAllUntreatedEmails() {
        Sort sort = Sort.by(Sort.Direction.ASC, "date");
        List<Email> emails = emailRepository.findByTreatedFalse(sort);
        return emails.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    @Override
    public List<EmailDto> getAllUnaffectedEmails(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size); // PageRequest starts from 0
        List<Email> emails = emailRepository.findByTreatedFalse(pageable);
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
    public ResponseEntity<String> deleteEmails(String[] ids) {
        try {
        for (String id : ids) {
            Optional<Email> email = emailRepository.findById(id);
            if (email.isPresent()) {
                EmailProcessingResult result = email.get().getResult();
                if (result.getRelatedActions() != null) {
                    actionParamsRepository.delete(result.getRelatedActions());
                }
                if (result != null) {
                    emailProcessingResultRepository.delete(result);
                }
                RelatedData relatedData = email.get().getRelatedData();
                if (relatedData != null) {
                    relatedDataRepository.delete(relatedData);
                }

                emailRepository.delete(email.get());
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.ok().build();
    } catch (Exception e) {
        logger.error("Error occurred while deleting Email: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete the resource"); // 500 Internal Server Error
        }
    }
    @Override
    public ResponseEntity<String> deleteEmail(String id) {
        try {
            Optional<Email> email = emailRepository.findById(id);
            if (email.isPresent()) {
                EmailProcessingResult result = email.get().getResult();
                if (result.getRelatedActions() != null) {
                    actionParamsRepository.delete(result.getRelatedActions());
                }
                if (result != null) {
                    emailProcessingResultRepository.delete(result);
                }
                RelatedData relatedData = email.get().getRelatedData();
                if (relatedData != null) {
                    relatedDataRepository.delete(relatedData);
                }

                emailRepository.delete(email.get());
                return ResponseEntity.ok().build();
            }
        } catch (Exception e) {
        logger.error("Error occurred while deleting Email: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete the resource"); // 500 Internal Server Error
        }
        return null;
    }
}
