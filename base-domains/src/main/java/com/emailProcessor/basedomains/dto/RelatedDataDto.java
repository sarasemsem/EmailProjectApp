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
public class RelatedDataDto implements Serializable {
    private String relatedDataId;
    private String account_number;
    private String account_type;
    private Instant period;
    private double amount;
    private String currency;
    private String recipient_account;

    @Override
    public String toString() {
        return "RelatedDataDto{" +
                "relatedDataId='" + relatedDataId + '\'' +
                ", accountNumber='" + account_number + '\'' +
                ", accountType='" + account_type + '\'' +
                ", period=" + period +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", recipientAccount='" + recipient_account + '\'' +
                '}';
    }
}
