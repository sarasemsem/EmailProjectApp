package com.project.emailprocessorservice;
import com.project.emailprocessorservice.service.EmailMonitoringService;
import com.project.emailprocessorservice.service.EmailProcessingService;
import com.sun.mail.imap.IMAPFolder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Session.class, Store.class, IMAPFolder.class})
public class EmailMonitoringServiceTest {

    @Mock
    private Session mockSession;

    @Mock
    private Store mockStore;

    @Mock
    private IMAPFolder mockInbox;
    @Mock
    private EmailProcessingService emailProcessingService;

    @Mock
    private Message mockMessage;

    @InjectMocks
    private EmailMonitoringService emailMonitoringService;

    @Test
    public void testScheduledEmailCheck() throws MessagingException, IOException {
        // Mocking behavior
        mockStatic(Session.class, Store.class, IMAPFolder.class);
        PowerMockito.when(Session.getDefaultInstance(any(Properties.class))).thenReturn(mockSession);
        PowerMockito.when(mockSession.getStore(eq("imaps"))).thenReturn(mockStore);
        PowerMockito.doNothing().when(mockStore).connect(anyString(), anyString());

        PowerMockito.when(mockStore.getFolder(eq("INBOX"))).thenReturn(mockInbox);
        PowerMockito.doReturn(true).when(mockInbox).open(Folder.READ_WRITE);

        PowerMockito.when(mockInbox.search(any(FlagTerm.class))).thenReturn(new Message[]{mockMessage});

        // Execute the method to be tested
        emailMonitoringService.scheduledEmailCheck();

        // Verify that the processMessage method is called
        verify(emailProcessingService, times(1)).processMessage(mockMessage);

        // Other verifications or assertions as needed
    }
}
