package com.emailProcessor.emailProcessor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.emailProcessor.emailProcessor.entity.Email;
import com.emailProcessor.emailProcessor.repository.EmailRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class EmailRepositoryTest {

    @Autowired
    private EmailRepository emailRepository;

    @Test
    @Transactional
    public void testInsertEmail() {
        // Create an Email object with sample data
        Email email = new Email();
        email.setSubject("Test Subject");
        email.setSender("test@example.com");
        email.setDate(Instant.now());
        email.setContent("This is a test email content.");

        // Save the email to the database
        emailRepository.save(email);

        // Retrieve the saved email from the database
        Optional<Email> savedEmail = emailRepository.findById(email.getEmailId());

        // Assert that the saved email is not null and contains the expected data
        assertNotNull(savedEmail.orElse(null));
        assertEquals("Test Subject", savedEmail.get().getSubject());
        assertEquals("test@example.com", savedEmail.get().getSender());
        assertEquals("This is a test email content.", savedEmail.get().getContent());
    }
}
