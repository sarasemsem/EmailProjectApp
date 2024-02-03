package com.project.emailProcessorService.service;

import com.emailProcessor.basedomains.dto.EmailDto;
import com.emailProcessor.basedomains.dto.EmailEvent;
import com.sun.mail.imap.IMAPFolder;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.*;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailMonitoringService extends MessageCountAdapter implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private EmailProducer emailProducer;

    private Session session;
    private String username;
    private String password;

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
                                EmailDto emailDto = processMessage(message);

                                // Produce the email to the Kafka topic
                                EmailEvent emailEvent = new EmailEvent();
                                emailEvent.setStatus("PENDING");
                                emailEvent.setMessage("email satus is in pending");
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
            throw new RuntimeException(e);
        }
    }

    public static String emailExtractor(String input) {
        // Define a regular expression pattern for extracting email addresses
        String emailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        Pattern pattern = Pattern.compile(emailRegex);

        // Create a matcher object
        Matcher matcher = pattern.matcher(input);

        // Find the first match
        if (matcher.find()) {
            return matcher.group();
        } else {
            // If no match is found, return null or handle it as needed
            return null;
        }
    }

    @Transactional
    public EmailDto processMessage(Message message) throws MessagingException, IOException {
        System.out.println("Im in the processMessage()");
        EmailDto emailDto = new EmailDto();
        emailDto.setSubject(message.getSubject());
        emailDto.setTreated(false);
        emailDto.setSender(emailExtractor(message.getFrom()[0].toString()));
        if (message.getReceivedDate() != null) {
            emailDto.setDate(message.getReceivedDate().toInstant());
        } else {
            // Handle the case where the received date is not available
        }
        Object content = message.getContent();
        if (content instanceof Multipart) {
            Multipart multipart = (Multipart) content;
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String bodyPartContent = bodyPart.getContent().toString();

                // Use Jsoup to parse HTML and extract text
                Document doc = Jsoup.parse(bodyPartContent);
                String textContent = doc.text();

                // Handle the textContent as needed
                emailDto.setContent(textContent);
            }
        } else {
            emailDto.setContent(content.toString());
        }
        System.out.println("Im in the email()" + emailDto.getDate() + emailDto.getSubject());
        return emailDto;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        retrieveNewEmails();
    }
}
