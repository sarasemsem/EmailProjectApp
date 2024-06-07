package com.emailProcessor.emailProcessor.controller;

import com.emailProcessor.basedomains.dto.EmailDto;
import com.emailProcessor.emailProcessor.controller.errors.BadRequestException;
import com.emailProcessor.emailProcessor.entity.Email;
import com.emailProcessor.emailProcessor.repository.EmailRepository;
import com.emailProcessor.emailProcessor.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("api/v1/email")
@RequiredArgsConstructor
public class EmailController {

    private final ModelMapper modelMapper;
    @Autowired
    private EmailService emailService;
    private final EmailRepository emailRepository;
    private static final String ENTITY_NAME = "Email";
    @GetMapping("retrievedEmails")
    public List<EmailDto> getEmails() {
        List<EmailDto> emails = emailService.getAllEmails();
        System.out.println("liste des emails"+emails);
        return emails;
    }

    @GetMapping("/treatedEmails")
    public List<EmailDto> getTreatedEmails() {
        List<EmailDto> emails = emailService.getTreatedEmails();
        System.out.println(emails.toString());
        return emails;
    }

    @GetMapping("/untreatedEmails")
    public List<EmailDto> getUntreatedEmails() {
        List<EmailDto> emails = emailService.getAllUntreatedEmails();
        System.out.println(emails.toString());
        return emails;
    }

    @GetMapping("/urgentEmails")
    public List<EmailDto> urgentEmails() {
        List<EmailDto> emails = emailService.getUrgentEmails();
        System.out.println(emails.toString());
        return emails;
    }

    @GetMapping("/{emailId}")
    public EmailDto getEmailDetails(@PathVariable Map<String,String> payload) throws InterruptedException {
        return emailService.getEmailById(payload.get("emailId"));
    }
    @PostMapping
    public ResponseEntity<String> saveEmail(@RequestBody EmailDto emailDto) {
        Email savedEmail = emailService.createEmail(emailDto);
        if (savedEmail != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Email inserted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to insert the Email");
        }
    }

    @PutMapping("/{id}")
    public EmailDto updateEmail(@PathVariable String id, @RequestBody EmailDto emailDto) {
        return emailService.updateEmail(id, emailDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Optional<Email>> emailPartialUpdate(@PathVariable String id, @RequestBody EmailDto emailDto) {
        System.out.println("REST request to partial update Sender partially :"+ id+ emailDto);
        if (emailDto.getEmailId() == null) {
            throw new BadRequestException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, emailDto.getEmailId())) {
            throw new BadRequestException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!emailRepository.existsById(id)) {
            throw new BadRequestException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        Optional<Email> result = emailService.partialUpdate(emailDto);

        return ResponseEntity.ok(Optional.of(result.get()));
    }

    @DeleteMapping("/delete/{ids}")
    public ResponseEntity<String> deleteEmails(@PathVariable String[] ids) {
        clearCache();
        System.out.println("your here");
        emailService.deleteEmails(ids);
        clearCache();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/clear_cache")
    @CacheEvict(value = "emails", allEntries = true )
    public String clearCache(){
        return "Cache has been cleared";
    }
}
