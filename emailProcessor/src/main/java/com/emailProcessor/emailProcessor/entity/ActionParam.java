package com.emailProcessor.emailProcessor.entity;

import com.emailProcessor.basedomains.dto.ActionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "actionParam")
public class ActionParam implements Serializable {
    @MongoId
    private String actionParamId;
    @Indexed
    @DBRef
    private Action action;
    @Indexed
    private Map<String, String> params;
    private Boolean affected;
    private Instant actionDate;

}