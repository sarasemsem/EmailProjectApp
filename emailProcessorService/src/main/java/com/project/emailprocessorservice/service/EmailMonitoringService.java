package com.project.emailprocessorservice.service;

import com.emailProcessor.basedomains.dto.EmailDto;
import com.emailProcessor.basedomains.dto.EmailEvent;
import com.sun.mail.imap.IMAPFolder;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.mail.*;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class EmailMonitoringService extends MessageCountAdapter implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private EmailProducer emailProducer;

    @Autowired
    private EmailProcessingService emailProcessingService;
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailMonitoringService.class);
    private final Session session;
    private final String username;
    private final String password;

    public EmailMonitoringService(Session session, String username, String password) {
        this.session = session;
        this.username = username;
        this.password = password;
    }

    @Scheduled(fixedRate = 6000) // Scheduled to run every 60 seconds, adjust as needed
    public void scheduledEmailCheck() {
        retrieveNewEmails();
    }

    @PostConstruct
    public void retrieveNewEmails() {
        try {
            System.out.println("Im in the retrieveNewEmails()");
            // Connect to the store
            Store store = session.getStore("imaps");
            // Connect to your inbox
            store.connect(username, password);
            IMAPFolder inbox = (IMAPFolder) store.getFolder("INBOX");

            // Open the inbox in read-write mode
            inbox.open(Folder.READ_WRITE);

            // Create a new thread to keep the connection alive
            Thread keepAliveThread = new Thread(new KeepAliveRunnable(inbox), "IdleConnectionKeepAlive");
            keepAliveThread.start();
            inbox.addMessageCountListener(
                new MessageCountAdapter() {
                    @Override
                    public void messagesAdded(MessageCountEvent event) {
                        // Process the newly added messages
                        Message[] messages = event.getMessages();
                        for (Message message : messages) {
                            try {
                                EmailDto emailDto = emailProcessingService.processMessage(message);
                                // Produce the email to the Kafka topic
                                EmailEvent emailEvent = new EmailEvent();
                                emailEvent.setStatus("NEW");
                                emailEvent.setMessage("new email coming-in");
                                emailEvent.setEmail(emailDto);
                                emailProducer.sendMessage(emailEvent);
                                System.out.println("Email sent to Kafka: " + emailDto.getSubject());

                            } catch (MessagingException | IOException e) {
                                e.printStackTrace();
                                System.out.println("Failed to process email.");
                            }
                        }
                    }
                }
            );
            // Schedule a task to check for new messages periodically
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(
                () -> {
                    try {
                        // Check for new messages using idle
                        System.out.println("Starting IDLE");
                        inbox.idle();
                    } catch (MessagingException e) {
                        System.out.println("Messaging exception during IDLE");
                        e.printStackTrace();
                    }
                },
                0,
                1,
                TimeUnit.MINUTES
            );
            // Wait for the scheduler to shut down before exiting
            scheduler.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            // Interrupt and shutdown the keep-alive thread
            if (keepAliveThread.isAlive()) {
                keepAliveThread.interrupt();
            }
            // Close the folder and store
            inbox.close(false);
            store.close();
        } catch (MessagingException | InterruptedException e) {
            e.printStackTrace(); // Log the exception
            LOGGER.warn("Interrupted!", e);
            /* Clean up whatever needs to be handled before interrupting  */
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        retrieveNewEmails();
    }
}
