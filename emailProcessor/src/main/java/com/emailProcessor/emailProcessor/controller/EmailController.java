package com.emailProcessor.emailProcessor.controller;

import com.emailProcessor.basedomains.dto.EmailDto;
import com.emailProcessor.emailProcessor.service.EmailService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;
    @RequestMapping("retrievedEmails")
    public List<EmailDto> getEmails() {
        return emailService.getAllEmails();
    }
    @PostMapping
    public ResponseEntity<String> saveEmail(@RequestBody EmailDto emailDto) {
        return emailService.createEmail(emailDto);
    }

    @PutMapping("/{id}")
    public EmailDto updateUser(@PathVariable ObjectId id, @RequestBody EmailDto emailDto) {
        return emailService.updateEmail(id, emailDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable ObjectId id) {
        emailService.deleteEmail(id);
        return ResponseEntity.noContent().build();
    }
}
