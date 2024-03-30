package com.emailProcessor.emailProcessor;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
@SpringBootApplication
@EnableCaching
@OpenAPIDefinition(info = @Info (title = "Library Configuration",version = "1.0", description = "Library Management Apis"))
public class EmailProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmailProcessorApplication.class, args);
	}
}

