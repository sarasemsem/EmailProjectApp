package com.emailProcessor.emailProcessor.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.emailProcessor.basedomains.dto.CategoryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

//@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "email")
public class Email implements Serializable {

    @Id
    private String emailId;
    private String sender;
    private String subject;
    private String originalContent;
    private String content;
    private Boolean isRead = false;
    private Instant date;
    @DBRef
    private EmailProcessingResult result;
    private Boolean treated = false;
    @DBRef
    private Sender contact;
}
