package com.project.emailprocessorservice;
import com.emailProcessor.basedomains.dto.EmailDto;
import com.project.emailprocessorservice.service.EmailProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.mail.*;
import javax.mail.internet.InternetAddress;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailMonitoringServiceTest {


    @InjectMocks
    private EmailProcessingService emailProcessingService;

    @Mock
    private Message message;

    @Mock
    private Multipart multipart;

    @Mock
    private Part part;

    @Mock
    private BodyPart bodyPart;
    @BeforeEach
    public void setUp() throws Exception {
        Address[] fromAddresses = {new InternetAddress("test@example.com")};
        when(message.getFrom()).thenReturn(fromAddresses);
        when(message.getSubject()).thenReturn("Test Subject");
        when(message.getReceivedDate()).thenReturn(new Date());
        lenient().when(message.getContent()).thenReturn(multipart);
        lenient().when(multipart.getCount()).thenReturn(1);
        lenient().when(multipart.getBodyPart(0)).thenReturn(bodyPart);
        lenient().when(bodyPart.getContentType()).thenReturn("text/plain");
        lenient().when(bodyPart.getContent()).thenReturn("Test content");
        lenient().when(bodyPart.getDisposition()).thenReturn(null);
        lenient().when(bodyPart.getFileName()).thenReturn(null);
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    public void testProcessMessage() throws Exception {
        assertNotNull(emailProcessingService.processMessage(message));
    }
}