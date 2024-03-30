package com.emailProcessor.emailProcessor.entity;

import java.io.Serializable;
import java.time.Instant;
import com.mongodb.lang.NonNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.redis.core.RedisHash;

//@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "email")
public class Email implements Serializable {

    @Id
    @Indexed(unique = true)
    private String emailId;
    @Indexed
    private String sender;
    @Indexed
    private String subject;
    @Indexed
    private String originalContent;
    @Indexed
    private String content;
    @Indexed
    private Boolean isRead = false;
    @Indexed
    private Instant date;
    @Indexed(sparse = true)
    @DBRef
    private EmailProcessingResult result;
    @Indexed
    private Boolean treated = false;
    @DBRef
    private Sender contact;
}
