package com.project.emailprocessorservice;

import com.project.emailprocessorservice.service.EmailMonitoringService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import javax.mail.Session;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@EmbeddedKafka(partitions = 1, controlledShutdown = false, topics = {"email-events"})
@TestPropertySource(properties = {
		"spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
		"email.host=imap.gmail.com",
		"email.port=993",
		 "email.protocol=imaps",
		"email.username=mejbrisara@gmail.com",
		"email.password=fyle kbhb uxrp ufis"
})
public class EmailProcessorServiceApplicationTests {

	@Autowired
	private EmailMonitoringService emailMonitoringService;

	@Autowired
	private Session mailSession;

	@Test
	public void contextLoads() {
		assertNotNull(emailMonitoringService);
		assertNotNull(mailSession);
	}

	@Test
	@Timeout(value = 60, unit = TimeUnit.SECONDS)  // Timeout of 60 seconds
	public void testRetrieveNewEmails() {
		emailMonitoringService.retrieveNewEmails();
		// Add assertions or verifications as needed
	}
}