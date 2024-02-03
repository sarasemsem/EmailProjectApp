package com.project.emailProcessorService.configuration;

import com.project.emailProcessorService.service.EmailMonitoringService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.mail.Session;
import java.util.Properties;

@Configuration
public class EmailConfig {

    @Value("${email.host}")
    private String emailHost;

    @Value("${email.port}")
    private String emailPort;

    @Value("${email.username}")
    private String emailUsername;

    @Value("${email.password}")
    private String emailPassword;
    private Session session;

    @Bean
    public Session mailSession() {
        if (session == null) {
            Properties props = new Properties();
            props.setProperty("mail.store.protocol", "imaps");
            props.setProperty("mail.imaps.host", emailHost);
            props.setProperty("mail.imaps.port", emailPort);

            // Create a new session with the properties
            session = Session.getInstance(props);
            session.setDebug(true); // Enable debug mode for troubleshooting
        }
        return session;
    }

    @Bean
    public EmailMonitoringService emailMonitoringService() {
        return new EmailMonitoringService(mailSession(), emailUsername, emailPassword);
    }
}