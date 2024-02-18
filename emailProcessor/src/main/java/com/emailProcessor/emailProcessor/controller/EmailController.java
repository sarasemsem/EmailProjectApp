package com.emailProcessor.emailProcessor.controller;

import com.emailProcessor.basedomains.dto.EmailDto;
import com.emailProcessor.emailProcessor.service.EmailService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200/")
@RequestMapping("api/v1/email")
public class EmailController {

    @Autowired
    private EmailService emailService;
    @RequestMapping("retrievedEmails")
    public List<EmailDto> getEmails() {
        return emailService.getAllEmails();
    }
    @GetMapping("/{emailId}")
    public EmailDto getEmailDetails(@PathVariable Map<String,String> payload) {
        return emailService.getEmailById(payload.get("emailId"));
    }
    @PostMapping
    public ResponseEntity<String> saveEmail(@RequestBody EmailDto emailDto) {
        return emailService.createEmail(emailDto);
    }

    @PutMapping("/{id}")
    public EmailDto updateEmail(@PathVariable String id, @RequestBody EmailDto emailDto) {
        return emailService.updateEmail(id, emailDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmail(@PathVariable String id) {
        emailService.deleteEmail(id);
        return ResponseEntity.noContent().build();
    }
}
