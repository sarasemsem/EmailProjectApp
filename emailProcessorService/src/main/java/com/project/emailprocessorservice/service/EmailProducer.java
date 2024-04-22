package com.project.emailprocessorservice.service;

import com.emailProcessor.basedomains.dto.EmailEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class EmailProducer {

private final NewTopic topic;
private static final Logger LOGGER = LoggerFactory.getLogger(EmailProducer.class);
private final KafkaTemplate<String, EmailEvent> kafkaTemplate;

    public EmailProducer(NewTopic topic, KafkaTemplate<String, EmailEvent> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(EmailEvent event){
     LOGGER.info(String.format("Email event => %s", event.toString()));
    // create Message
        Message<EmailEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC,topic.name())
                .build();
        kafkaTemplate.send(message);
    }
}
