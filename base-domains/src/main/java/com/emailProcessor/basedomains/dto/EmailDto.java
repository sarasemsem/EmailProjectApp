package com.emailProcessor.basedomains.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailDto implements Serializable {

    private String emailId;
    private String sender;
    private String subject;
    private String originalContent;
    private String content;
    private Boolean isRead = false;
    private Boolean urgent = false;
    private Boolean important = false;
    private Boolean draft = false;
    private Boolean spam = false;
    private Boolean archived = false;
    private Instant date;
    private EmailProcessingResultDto result;
    private Boolean treated = false;
    private SenderDto contact;
}
