package com.emailProcessor.emailProcessor.service;

import com.emailProcessor.emailProcessor.configuration.ActionParamEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class ActionParamsProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionParamsProducer.class);
    private final NewTopic topic;
    private final KafkaTemplate<String, ActionParamEvent> kafkaTemplate;

    public ActionParamsProducer(NewTopic topic, KafkaTemplate<String, ActionParamEvent> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(ActionParamEvent event) {
        LOGGER.info(String.format("Email event => %s", event.toString()));
        // create Message
        Message<ActionParamEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topic.name())
                .build();

        CompletableFuture<SendResult<String, ActionParamEvent>> future = kafkaTemplate.send(message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                RecordMetadata metadata = result.getRecordMetadata();
                LOGGER.info("Message sent successfully with key {} to partition {} with offset {}",
                        result.getProducerRecord().key(), metadata.partition(), metadata.offset());
            } else {
                LOGGER.error("Failed to send message with key {}", event, ex);
            }
        });
    }
}
