package com.emailProcessor.emailProcessor.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "relatedData")
public class RelatedData implements Serializable {
    @MongoId
    private String relatedDataId;
    private String account_number;
    private String account_type;
    private Instant period;
    private double amount;
    private String currency;
    private String recipient_account;


}
