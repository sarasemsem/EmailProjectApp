package com.project.emailProcessorService;

import com.project.emailProcessorService.configuration.EmailConfig;
import com.project.emailProcessorService.service.EmailMonitoringService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.project.emailProcessorService")
public class EmailProcessorServiceApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(EmailProcessorServiceApplication.class, args);
	}
	@Override
	public void run(String... args) throws Exception {
		ApplicationContext context = new AnnotationConfigApplicationContext(EmailConfig.class);

		EmailMonitoringService emailListener = context.getBean(EmailMonitoringService.class);
		Thread.sleep(5000); // Sleep for 5 seconds
		emailListener.retrieveNewEmails();

	}
}
