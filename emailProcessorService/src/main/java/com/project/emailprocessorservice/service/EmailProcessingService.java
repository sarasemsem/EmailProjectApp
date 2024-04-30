package com.project.emailprocessorservice.service;

import com.emailProcessor.basedomains.dto.EmailDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;

@Service
public class EmailProcessingService {

    @Transactional
    public EmailDto processMessage(Message message) throws MessagingException, IOException {
        System.out.println("Im in the processMessage()");
        EmailDto emailDto = new EmailDto();
        String subject;
        if (message.getSubject().isEmpty()) {
            subject="No subject";
        } else subject = message.getSubject();
        emailDto.setSubject(subject);
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
            StringBuilder htmlContent = new StringBuilder();

            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);

                String bodyPartContent = bodyPart.getContent().toString();
                // Use Jsoup to parse HTML and extract text
                Document doc = Jsoup.parse(bodyPartContent);
                String textContent = doc.text();
                // Handle the textContent as needed
                emailDto.setContent(textContent);

                // Check if the body part is HTML
                if (bodyPart.isMimeType("text/html")) {
                    // Append HTML content
                    htmlContent.append(bodyPart.getContent().toString());
                }

            }
            emailDto.setOriginalContent(htmlContent.toString());
        } else {
            //emailDto.setContent(content.toString());
            emailDto.setOriginalContent(message.getContent().toString());
        }
        System.out.println("Im in the email()" + emailDto.getDate() + emailDto.getSubject());
        return emailDto;
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
}