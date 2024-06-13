package com.emailProcessor.emailProcessor.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "attachment")
public class Attachment implements Serializable {
    @Id
    @Field("_id")
    private String attachmentId;
    private String fileName;
    private String fileId ;
    private byte[] fileContent;
}
