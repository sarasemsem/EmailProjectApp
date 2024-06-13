package com.emailProcessor.basedomains.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentDto implements Serializable {

    private String attachmentId;
    private String fileName;
    private String fileId ;
    private byte[] fileContent;
}
