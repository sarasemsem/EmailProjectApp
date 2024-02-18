package com.emailProcessor.emailProcessor.entity;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "email")
public class Email {

    @Id
    private String emailId;
    private String sender;
    private String subject;
    private String originalContent;
    private String content;
    private Boolean isRead = false;
    private Instant date;
    private Integer categoryId;
    private Boolean treated = false;
}
