package com.emailProcessor.emailProcessor.service;

import com.emailProcessor.basedomains.dto.EmailDto;
import com.emailProcessor.basedomains.dto.EmailEvent;
import com.emailProcessor.emailProcessor.entity.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EmailConsumer {
    @Autowired
    private EmailService emailService;
    @Autowired
    private NLPService nlpService;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailConsumer.class);
    @KafkaListener(
            topics="${spring.kafka.topic.name}"
            , groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(EmailEvent event){
    LOGGER.info(String.format("Email event recieved in processor service => %s", event.toString()));
        //save the email into the database
        consumeEmail(event.getEmail());
        try {
            nlpService.treatment();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void consumeEmail(EmailDto emailDto) {
        try {
            // Save email to the database
            Email savingEmail= emailService.createEmail(emailDto);
            System.out.println("Email saving to the database: " + savingEmail);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to save email to the database.");
        }
    }
}
