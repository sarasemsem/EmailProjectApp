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

    //private Integer emailId;
    private String sender;
    private String subject;

    // @Column(columnDefinition = "LONGTEXT")
    private String content;

    private Instant date;
    private Integer categoryId;
    private Boolean treated;
}
