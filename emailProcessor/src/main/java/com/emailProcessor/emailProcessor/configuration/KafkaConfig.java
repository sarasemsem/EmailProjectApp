package com.emailProcessor.emailProcessor.configuration;

import com.emailProcessor.emailProcessor.serializer.CategoryDtoKeyDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.serializer.Deserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaConfig {

    @Bean
    public Deserializer categoryDtoKeyDeserializer() {
        return (Deserializer) new CategoryDtoKeyDeserializer();
    }

    @Bean
    public JsonDeserializer<Object> jsonDeserializer() {
        JsonDeserializer<Object> deserializer = new JsonDeserializer<>();
        deserializer.addTrustedPackages("*");
        return deserializer;
    }
}