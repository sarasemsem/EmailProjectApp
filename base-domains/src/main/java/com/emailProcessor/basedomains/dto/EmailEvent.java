package com.emailProcessor.basedomains.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailEvent {
    private String message;
    private String status;
    private EmailDto email;
}
