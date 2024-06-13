package com.emailProcessor.emailProcessor.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

//@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "email")
public class Email implements Serializable {

    @Id
    private String emailId;
    @Indexed
    private String sender;
    private String subject;
    private String originalContent;
    private String content;
    @Indexed
    private Boolean isRead = false;
    @Indexed
    private Boolean urgent = false;
    @Indexed
    private Boolean important = false;
    private Boolean draft = false;
    private Boolean spam = false;
    private Boolean archived = false;
    @Indexed
    private Instant date;
    @Indexed(sparse = true)
    @DBRef
    private EmailProcessingResult result;
    @Indexed
    private Boolean treated = false;
    @DBRef
    private Sender contact;
    @DBRef
    private RelatedData relatedData;
    @DBRef
    private List<Attachment> attachments;
    //private List<String> attachmentIds;
    //@DBRef
    //private Action relatedAction;
}
