package com.emailProcessor.emailProcessor.configuration;

import com.emailProcessor.emailProcessor.entity.ActionParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActionParamEvent {
    private String message;
    private String status;
    private ActionParam actionParam;
}