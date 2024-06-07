package com.emailProcessor.emailProcessor.entity;

import com.emailProcessor.basedomains.dto.ActionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Map;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "actionParam")
public class ActionParam implements Serializable {
    private ActionDto action;
    private Map<String, String> params;
}