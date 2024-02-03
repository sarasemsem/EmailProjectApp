package com.emailProcessor.emailProcessor.service;

import com.emailProcessor.basedomains.dto.EmailDto;
import com.emailProcessor.basedomains.dto.EmailEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EmailConsumer {
    @Autowired
    private EmailService emailService;
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailConsumer.class);
    @KafkaListener(
            topics="${spring.kafka.topic.name}"
            , groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(EmailEvent event){
    LOGGER.info(String.format("Email event recieved in processor service => %s", event.toString()));
    //save the email into the database
        consumeEmail(event.getEmail());
    }

    public void consumeEmail(EmailDto emailDto) {
        try {
            // Save email to the database
            emailService.createEmail(emailDto);
            System.out.println("Email saved to the database: " + emailDto.getSubject());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to save email to the database.");
        }
    }
}
