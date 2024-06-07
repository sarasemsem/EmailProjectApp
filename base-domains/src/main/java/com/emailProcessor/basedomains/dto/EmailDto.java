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
    private Instant date;
    private Boolean isRead;
    private Boolean treated;
    private Boolean urgent;
    private Boolean important;
    private Boolean draft;
    private Boolean spam;
    private Boolean archived;
    private SenderDto contact;
    private EmailProcessingResultDto result;
    private RelatedDataDto relatedData;
}
