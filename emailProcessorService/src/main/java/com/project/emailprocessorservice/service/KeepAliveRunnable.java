package com.project.emailprocessorservice.service;

import com.sun.mail.imap.IMAPFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;

public class KeepAliveRunnable implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(KeepAliveRunnable.class);
    private static final long KEEP_ALIVE_FREQ_MINUTES = 5;
    private static final long KEEP_ALIVE_FREQ = KEEP_ALIVE_FREQ_MINUTES * 60 * 1000; // Convert minutes to milliseconds

    private final IMAPFolder folder;

    public KeepAliveRunnable(IMAPFolder folder) {
        this.folder = folder;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(KEEP_ALIVE_FREQ);
                logger.info("Performing a NOOP to keep the connection alive");
                folder.doCommand(protocol -> {
                    protocol.simpleCommand("NOOP", null);
                    return null;
                });
            } catch (InterruptedException e) {
                // Ignore, just aborting the thread...
            } catch (MessagingException e) {
                logger.error("Unexpected exception while keeping alive the IDLE connection", e);
            }
        }
    }
}
