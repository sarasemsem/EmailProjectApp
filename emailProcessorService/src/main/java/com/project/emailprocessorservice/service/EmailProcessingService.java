package com.project.emailprocessorservice.service;

import com.emailProcessor.basedomains.dto.AttachmentDto;
import com.emailProcessor.basedomains.dto.EmailDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.*;

@Service
public class EmailProcessingService {

    @Transactional
    public EmailDto processMessage(Message message) throws Exception {
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
        List<AttachmentDto> attachments = new ArrayList<>();
        Object content = message.getContent();
        StringBuilder htmlContent = new StringBuilder();
        StringBuilder textContent = new StringBuilder();

        System.out.println("Email content type: " + message.getContentType());

        if (content instanceof Multipart) {
            Multipart multipart = (Multipart) content;
            System.out.println("Email is multipart with " + multipart.getCount() + " parts.");

            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                System.out.println("Processing part " + (i + 1) + " with content type: " + bodyPart.getContentType());

                if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) ||
                        (bodyPart.getFileName() != null && !bodyPart.getFileName().isEmpty())) {
                    // It's an attachment
                    attachments.add(processAttachment(bodyPart));
                } else {
                    // It's the email body
                    if (bodyPart.isMimeType("text/html")) {
                        htmlContent.append(bodyPart.getContent().toString());
                        System.out.println("Added HTML content: " + bodyPart.getContent().toString());
                    } else if (bodyPart.isMimeType("text/plain")) {
                        textContent.append(bodyPart.getContent().toString());
                        System.out.println("Added plain text content: " + bodyPart.getContent().toString());
                    } else if (bodyPart.isMimeType("multipart/*")) {
                        Multipart innerMultipart = (Multipart) bodyPart.getContent();
                        System.out.println("Found nested multipart with " + innerMultipart.getCount() + " parts.");

                        for (int j = 0; j < innerMultipart.getCount(); j++) {
                            BodyPart innerBodyPart = innerMultipart.getBodyPart(j);
                            System.out.println("Processing nested part " + (j + 1) + " with content type: " + innerBodyPart.getContentType());

                            if (innerBodyPart.isMimeType("text/html")) {
                                htmlContent.append(innerBodyPart.getContent().toString());
                                System.out.println("Added nested HTML content: " + innerBodyPart.getContent().toString());
                            } else if (innerBodyPart.isMimeType("text/plain")) {
                                textContent.append(innerBodyPart.getContent().toString());
                                System.out.println("Added nested plain text content: " + innerBodyPart.getContent().toString());
                            }
                        }
                    }
                }
            }
        } else if (content instanceof String) {
            // Handle single-part email
            textContent.append(content.toString());
            htmlContent.append(content.toString());
            System.out.println("Single-part email content: " + content.toString());
        }

        // Set email content
        emailDto.setOriginalContent(htmlContent.toString());
        emailDto.setContent(textContent.toString());

        // Set attachments
        emailDto.setAttachments(attachments);

        System.out.println("Final email content: " + emailDto.getContent());
        System.out.println("Final email originalContent: " + emailDto.getOriginalContent());

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
    private AttachmentDto processAttachment(BodyPart bodyPart) throws Exception {
        String fileName = bodyPart.getFileName();
        System.out.println("Email has attachment called: " + fileName);

        InputStream is = bodyPart.getInputStream();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int bytesRead;
        while ((bytesRead = is.read(buf)) != -1) {
            buffer.write(buf, 0, bytesRead);
        }
        AttachmentDto attachmentDto = new AttachmentDto();
        attachmentDto.setAttachmentId(UUID.randomUUID().toString());
        attachmentDto.setFileName(fileName);
        attachmentDto.setFileContent(buffer.toByteArray());

        return attachmentDto;
    }
}