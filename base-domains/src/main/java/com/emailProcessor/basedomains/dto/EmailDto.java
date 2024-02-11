package com.emailProcessor.basedomains.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailDto {

    private String emailId;
    private String sender;
    private String subject;
    private String content;
    private Instant date;
    private Integer categoryId;
    private Boolean treated;
}
